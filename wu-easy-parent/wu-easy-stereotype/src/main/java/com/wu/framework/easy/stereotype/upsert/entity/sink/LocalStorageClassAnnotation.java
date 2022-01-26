package com.wu.framework.easy.stereotype.upsert.entity.sink;

import com.wu.framework.easy.stereotype.upsert.EasySmart;
import com.wu.framework.inner.layer.CamelAndUnderLineConverter;
import com.wu.framework.inner.layer.data.SmartMark;
import com.wu.framework.inner.lazy.database.expand.database.persistence.domain.LazyTableAnnotation;
import lombok.Data;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * description 本地存储类注解
 *
 * @author Jia wei Wu
 * @date 2020/9/3 上午10:02
 */
@Data
public class LocalStorageClassAnnotation {

    public final static String DOMAIN_CLASS_TEMP =
            "package com.wu.framework.inner.lazy.database.test.pojo;\n" +
                    "\n" +
                    "import lombok.AllArgsConstructor;\n" +
                    "import lombok.Data;\n" +
                    "import lombok.NoArgsConstructor;\n" +
                    "import lombok.experimental.Accessors;\n" +
                    "import java.io.Serializable;\n" +
                    "@Accessors(chain = true)\n" +
                    "@Data\n" +
                    "@AllArgsConstructor\n" +
                    "@NoArgsConstructor"
                    + "\n";
    private static final String PREFIX = "easy_upsert_";
    //    private static final Logger log = LoggerFactory.getLogger(LocalStorageClassAnnotation.class);
    @Deprecated
    public static Map<Class, LazyTableAnnotation> CLASS_CUSTOM_TABLE_ANNOTATION_ATTR_MAP = new HashMap<>();
    public static Map<Class, EasySmart> CLASS_EASY_SMART_MAP = new HashMap<>();

    /**
     * @param clazz                      类
     * @param isForceDuplicateNameSwitch 是强制重复名称切换
     * @return
     * @describe
     * @author Jia wei Wu
     * @date 2021/3/3 9:34 下午
     **/
    @Deprecated
    public static EasySmart getEasyTableAnnotation(Class clazz, boolean isForceDuplicateNameSwitch) {
        return easySmart(clazz, isForceDuplicateNameSwitch);
    }

    /**
     * @param
     * @return
     * @describe 获取并且完善 LazyTable
     * @author Jia wei Wu
     * @date 2021/3/29 8:43 下午
     **/
    public static EasySmart easySmart(Class clazz, boolean isForceDuplicateNameSwitch) {
        if (!CLASS_EASY_SMART_MAP.containsKey(clazz)) {
            String kafkaTopicName = CamelAndUnderLineConverter.humpToLine2(clazz.getSimpleName());
            EasySmart easySmart = AnnotatedElementUtils.findMergedAnnotation(clazz, EasySmart.class);
            String kafkaCode = CamelAndUnderLineConverter.humpToLine2(clazz.getSimpleName());
            String className = clazz.getName();
            String tableName = CamelAndUnderLineConverter.humpToLine2(clazz.getSimpleName());
            String comment = "";
            String kafkaSchemaName = CamelAndUnderLineConverter.humpToLine2(clazz.getSimpleName());
            boolean perfectTable = false;
            boolean smartFillField = false;
            String nameSpace= "default";
            if (null != easySmart) {
                if (!ObjectUtils.isEmpty(easySmart.kafkaTopicName())) {
                    kafkaTopicName = easySmart.kafkaTopicName();
                }
                if (!ObjectUtils.isEmpty(easySmart.kafkaCode())) {
                    kafkaCode = easySmart.kafkaCode();
                }
                if (!ObjectUtils.isEmpty(easySmart.tableName())) {
                    tableName = easySmart.tableName();
                }
                if (!ObjectUtils.isEmpty(easySmart.kafkaSchemaName())) {
                    kafkaSchemaName = easySmart.kafkaSchemaName();
                }
                smartFillField = easySmart.smartFillField();
                perfectTable = easySmart.perfectTable();
                nameSpace=easySmart.namespace();
            }
            if (isForceDuplicateNameSwitch) {
                kafkaSchemaName = CamelAndUnderLineConverter.humpToLine2(clazz.getName().replace(".", "_"));
            }

            String finalTableName = tableName;
            String finalKafkaSchemaName = kafkaSchemaName;
            String finalKafkaTopicName = kafkaTopicName;
            String finalKafkaCode = kafkaCode;

            boolean finalSmartFillField = smartFillField;
            boolean finalPerfectTable = perfectTable;
            String finalNameSpace = nameSpace;
            CLASS_EASY_SMART_MAP.put(clazz, new EasySmart() {

                /**
                 * Returns the annotation type of this annotation.
                 *
                 * @return the annotation type of this annotation
                 */
                @Override
                public Class<? extends Annotation> annotationType() {
                    return EasySmart.class;
                }

                /**
                 * 表名
                 *
                 * @return
                 */
                @Override
                public String tableName() {
                    return finalTableName;
                }

                /**
                 * 完善表
                 *
                 * @return
                 */
                @Override
                public boolean perfectTable() {
                    return finalPerfectTable;
                }

                /**
                 * 数据下钻
                 * the field use Annotation with {@link SmartMark}
                 */
                @Override
                public boolean dataDrillDown() {
                    return null == easySmart ? false : easySmart.dataDrillDown();
                }

                /**
                 * 表注释
                 *
                 * @return String
                 */
                @Override
                public String comment() {
                    return comment;
                }

                /**
                 * kafka  schema 名称
                 *
                 * @return String
                 */
                @Override
                public String kafkaSchemaName() {
                    return finalKafkaSchemaName;
                }

                /**
                 * kafka 主题 为空使用类名
                 *
                 * @return String
                 */
                @Override
                public String kafkaTopicName() {
                    return finalKafkaTopicName;
                }

                /**
                 * kafka code编码
                 *
                 * @return String
                 */
                @Override
                public String kafkaCode() {
                    return finalKafkaCode;
                }

                /**
                 * 数据库名 schema
                 *
                 * @return String
                 */
                @Override
                public String schema() {
                    return null == easySmart ? "" : easySmart.schema();
                }

                /**
                 * Elasticsearch 索引前缀
                 *
                 * @return String
                 */
                @Override
                public String indexPrefix() {
                    return null == easySmart ? "" : easySmart.indexPrefix();
                }

                /**
                 * Elasticsearch 索引时间格式
                 *
                 * @return String
                 */
                @Override
                public String indexFormat() {
                    return null == easySmart ? "" : easySmart.indexFormat();
                }

                /**
                 * Elasticsearch 索引后缀
                 */
                @Override
                public String indexSuffix() {
                    return null == easySmart ? "" : easySmart.indexSuffix();
                }

                /**
                 * Elasticsearch 索引类型
                 */
                @Override
                public String indexType() {
                    return null == easySmart ? "" : easySmart.indexType();
                }

                /**
                 * redis key
                 */
                @Override
                public String redisKey() {
                    return null == easySmart ? finalTableName : easySmart.redisKey();
                }

                /**
                 * 列族
                 *
                 * @return
                 */
                @Override
                public String columnFamily() {
                    return null == easySmart ? "" : easySmart.columnFamily();
                }

                /**
                 * 智能填充bean属性
                 * 针对数据源 如mysql查询结果、http请求结果中包含的数据字段不再当前对象中
                 */
                @Override
                public boolean smartFillField() {
                    return finalSmartFillField;
                }

                /**
                 * 表空间
                 *
                 * @return
                 */
                @Override
                public String namespace() {
                    return finalNameSpace;
                }
            });
        }
        return CLASS_EASY_SMART_MAP.get(clazz);
    }

}
