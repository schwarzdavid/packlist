package app.schwarzdavid.packlist.common.annotation;

import app.schwarzdavid.packlist.common.PacklistCommonConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(PacklistCommonConfig.class)
public @interface EnablePacklistCommon {
}
