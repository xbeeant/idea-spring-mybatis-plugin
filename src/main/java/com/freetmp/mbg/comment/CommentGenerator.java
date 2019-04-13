package com.freetmp.mbg.comment;

import com.freetmp.mbg.i18n.Resources;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

/*
 * Created by LiuPin on 2015/2/14.
 */
public class CommentGenerator extends DefaultCommentGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(CommentGenerator.class);

    public static final String XMBG_CG_I18N_PATH_KEY = "i18n_path_key_for_CG";

    public static final String XMBG_CG_PROJECT_START_YEAR = "project_start_year_for_copyright";

    public static final String XMBG_CG_I18N_LOCALE_KEY = "i18n_locale_key_for_CG";

    public static final String XMBG_CG_I18N_DEFAULT_PATH = "i18n_for_CG";
    public static final String XMBG_CG_PROJECT_START_DEFAULT_YEAR;

    static {
        XMBG_CG_PROJECT_START_DEFAULT_YEAR = "" + Calendar.getInstance().get(Calendar.YEAR);
    }

    protected ThreadLocal<XmlElement> rootElement = new ThreadLocal<>();

    protected boolean suppressAllComments;
    protected boolean suppressDate;

    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    protected Resources comments;
    protected Resources defaultComments;

    protected Resources copyrights;
    protected Resources defaultCopyrights;

    protected String startYear;
    protected String endYear;

    protected String i18nPath = XMBG_CG_I18N_DEFAULT_PATH;

    public CommentGenerator() {
        super();
    }

    private void initResources(Locale locale) throws MalformedURLException {

        defaultComments = new Resources(XMBG_CG_I18N_DEFAULT_PATH + "/Comments",locale);
        defaultCopyrights = new Resources(XMBG_CG_I18N_DEFAULT_PATH + "/Copyrights",locale);

        ClassLoader loader = getClass().getClassLoader();

        // add user specified i18n sources directory to the classpath
        if(!i18nPath.equals(XMBG_CG_I18N_DEFAULT_PATH)) {
            URL[] urls = {new File(i18nPath).toURI().toURL()};
            loader = new URLClassLoader(urls);
            comments = new Resources("Comments",locale,loader);
            copyrights = new Resources("Copyrights",locale,loader);
        }else {
            comments = defaultComments;
            copyrights = defaultCopyrights;
        }

        endYear = "" + Calendar.getInstance().get(Calendar.YEAR);
    }

    /*
     * This method returns a formated date string to include in the Javadoc tag
     * and XML comments. You may return null if you do not want the date in
     * these documentation elements.
     *
     * @return a string representing the current timestamp, or null
     */
    @Override
    protected String getDateString() {
        return sdf.format(new Date());
    }

    @Override
    public void addConfigurationProperties(Properties properties) {
        super.addConfigurationProperties(properties);

        // stolen from the parent
        suppressDate = isTrue(properties
                .getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_DATE));

        suppressAllComments = isTrue(properties
                .getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS));

        if(suppressAllComments) {
            return;
        }

        // 获取国际化资源的路径
        i18nPath = properties.getProperty(XMBG_CG_I18N_PATH_KEY, XMBG_CG_I18N_DEFAULT_PATH);
        LOG.info("use the i18n resources under {}",i18nPath);

        // 获取项目开始时间，用在版权声明中
        String startYearStr = properties.getProperty(XMBG_CG_PROJECT_START_YEAR);
        if(StringUtils.isNotEmpty(startYearStr)){
            startYear = startYearStr;
        }else{
            startYear = XMBG_CG_PROJECT_START_DEFAULT_YEAR;
        }

        // 初始化资源
        String localeStr = properties.getProperty(XMBG_CG_I18N_LOCALE_KEY);
        Locale locale = Locale.getDefault();
        if(localeStr != null && StringUtils.isNoneEmpty(localeStr)) {
            String[] localeAras = localeStr.trim().split("_");
            locale = new Locale(localeAras[0], localeAras[1]);
        }
        LOG.info("use the locale {}",locale);
        try {
            initResources(locale);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {

        // if user doesn't supplied the java source copyright then use the default
        String copyright = copyrights.getFormatted("JavaSource", startYear, endYear);
        if(StringUtils.isEmpty(copyright)){
            copyright = defaultCopyrights.getFormatted("JavaSource",startYear,endYear);
            if(StringUtils.isEmpty(copyright)) {
                return;
            }
        }

        String[] array = copyright.split("\\|");

        for(String str : array){
            if(str.startsWith("*")){
                str = " " + str;
            }
            compilationUnit.addFileCommentLine(str);
        }
    }
}
