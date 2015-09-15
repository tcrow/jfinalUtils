

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 作者 tcrow 创建时间 19/06/2015.
 * 功能描述：
 * 表单提交时接收数据可以使用此切面方法，用以绑定表单数据到bean里面，获取时可以直接用getAttr("bean")，
 * 然后强制转换成对应的bean即可，暂不支持多个bean的绑定方式
 * 使用另外使用时需要加上Annotation注解@BindData(ControllerBean.class)，
 * 注解里面的value只要是ControllerBean的实现Bean都可以，
 * 同时要在方法上面申明方法级别的拦截器@Before(BindDataAOP.class),
 * 有一点需要注意，为了方便直接获取Bean对象同时减少转换Json的开销，数据直接放入了attr中，
 * 因此如果不想最后返回给api调用方则需要清除该bean，清除方法removeAttr("bean");
 * 示例代码：
 * @Before(BindDataAOP.class)
 * @BindData(ApplyBean.clsss)
 *  public void applyPage(){
 *      ApplyBean bean = (ApplyBean)getAttr("bean");
 *      System.out.println(bean.getPrgName());
 *      removeAttr("bean");
 *  }
 * 修改记录：
 *
 */
public class BindDataAOP implements Interceptor {
    private final static Logger log = Logger.getLogger(BindDataAOP.class);

    @Override
    public void intercept(Invocation inv) {
        Method method = inv.getMethod();
        BindData annotation = method.getAnnotation(BindData.class);
        if(null == annotation){
            inv.invoke();
            return;
        }
        Controller controller = inv.getController();
        Class clasz = annotation.value();
        ControllerBean bean = null;
        try {
            bean = (ControllerBean)clasz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            log.error(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            log.error(e);
        }
        Field[] declaredFields = clasz.getDeclaredFields();
        Method[] methods = clasz.getMethods();
        String methodName = null;
        String declaredFieldName = null;
        String methodNameEndWord = null;
        for (Field declaredField : declaredFields) {
            declaredFieldName = declaredField.getName();
            for (Method method1 : methods) {
                methodName = method1.getName();
                methodNameEndWord = declaredFieldName.substring(0, 1).toUpperCase() + declaredFieldName.substring(1);
                if(methodName.startsWith("set") && methodName.endsWith(methodNameEndWord)){
                    try {
                        method1.invoke(bean,controller.getPara(declaredFieldName));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        log.error(e);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                        log.error(e);
                    }
                }
            }
        }
        controller.setAttr("bean",bean);
        inv.invoke();
    }
}
