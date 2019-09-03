package com.ecfront.dew.common;

import com.ecfront.dew.common.interceptor.DewInterceptContext;
import com.ecfront.dew.common.interceptor.DewInterceptor;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

/**
 * The type Interceptor test.
 *
 * @author gudaoxuri
 */
public class InterceptorTest {

    /**
     * Test interceptor.
     */
    @Test
    public void testInterceptor() {
        // 没有注册拦截器的情况
        Resp<DewInterceptContext<Obj, Obj>> resp =
                $.interceptor.process("none", new Obj("1"), new HashMap<>(), context -> {
                    // 业务逻辑，只做简单将input对象copy到output对象
                    context.setOutput($.bean.copyProperties(context.getInput(), Obj.class));
                    return Resp.success(context);
                });
        Assert.assertTrue(resp.ok());
        Assert.assertEquals("1", resp.getBody().getOutput().getF());
        // 注册了一个拦截器A
        $.interceptor.register("test", new InterceptorA());
        resp = $.interceptor.process("test", new Obj("1"), new HashMap<>(), context -> {
            // 业务逻辑，只做简单将input对象copy到output对象
            context.setOutput($.bean.copyProperties(context.getInput(), Obj.class));
            return Resp.success(context);
        });
        Assert.assertTrue(resp.ok());
        Assert.assertEquals("2", resp.getBody().getInput().getF());
        Assert.assertEquals("3", resp.getBody().getOutput().getF());
        // 注册了另一个拦截器B，假设B执行会报错
        $.interceptor.register("test", new InterceptorB());
        resp = $.interceptor.process("test", new Obj("11"), new HashMap<>(), context -> {
            // 业务逻辑，只做简单将input对象copy到output对象
            context.setOutput($.bean.copyProperties(context.getInput(), Obj.class));
            return Resp.success(context);
        });
        Assert.assertTrue(!resp.ok());
    }


    /**
     * The type Obj.
     */
    public static class Obj {
        private String f;

        /**
         * Instantiates a new Obj.
         */
        public Obj() {
        }

        /**
         * Instantiates a new Obj.
         *
         * @param f the f
         */
        public Obj(String f) {
            this.f = f;
        }

        /**
         * Gets f.
         *
         * @return the f
         */
        public String getF() {
            return f;
        }

        /**
         * Sets f.
         *
         * @param f the f
         */
        public void setF(String f) {
            this.f = f;
        }
    }

    /**
     * The type Interceptor a.
     */
    public class InterceptorA implements DewInterceptor<Obj, Obj> {

        @Override
        public String getCategory() {
            return "test";
        }

        @Override
        public String getName() {
            return "A";
        }

        @Override
        public Resp<DewInterceptContext<Obj, Obj>> before(DewInterceptContext<Obj, Obj> context) {
            context.getInput().setF("2");
            return Resp.success(context);
        }

        @Override
        public Resp<DewInterceptContext<Obj, Obj>> after(DewInterceptContext<Obj, Obj> context) {
            context.getOutput().setF("3");
            return Resp.success(context);
        }

    }

    /**
     * The type Interceptor b.
     */
    public class InterceptorB implements DewInterceptor<Obj, Obj> {

        @Override
        public String getCategory() {
            return "test";
        }

        @Override
        public String getName() {
            return "B";
        }

        @Override
        public Resp<DewInterceptContext<Obj, Obj>> before(DewInterceptContext<Obj, Obj> context) {
            return Resp.badRequest("some error");
        }

        @Override
        public Resp<DewInterceptContext<Obj, Obj>> after(DewInterceptContext<Obj, Obj> context) {
            return Resp.success(context);
        }

        @Override
        public void error(DewInterceptContext<Obj, Obj> context) {
            context.getInput().setF("error");
        }
    }


}
