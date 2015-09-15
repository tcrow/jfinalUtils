import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 作者 tcrow 创建时间 19/06/2015.
 * 功能描述：
 * Contorl绑定Bean数据标签
 * 修改记录：
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindData {
    Class value() default ControllerBean.class;
}
