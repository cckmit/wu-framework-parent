package com.wu.freamwork.controller.ws.domian;


import com.wu.framework.easy.stereotype.upsert.EasySmart;
import com.wu.framework.easy.stereotype.upsert.EasySmartField;
import lombok.Data;

@Data
@EasySmart(value = "rn_rcm_csmt_pers_punish", comment = "25421信用_公路建设市场人员行政处罚信息表")
public class PersonPunish {


    @EasySmartField(value = "data_id", type = "VARCHAR2(200)", comment = " guid ")
    private String ID;
    @EasySmartField(value = "administrative_counterpart_name", type = "VARCHAR2(200)", comment = " 行政相对人名称 ")
    private String XINGZHENGXIANGDUIRMC;
    @EasySmartField(value = "unified_social_credit_code", type = "VARCHAR2(18)", comment = " 行政相对人代码_1（统一社会信用代码）统一社会信用代码 ")
    private String XINGZHENGXIANGDUIRDM_1TYSHXYDM;
    @EasySmartField(value = "business_registration_number", type = "VARCHAR2(50)", comment = " 行政相对人代码_2（工商注册号）工商注册号 ")
    private String XINGZHENGXIANGDUIRDM_2GSZCH;
    @EasySmartField(value = "organization_code", type = "VARCHAR2(9)", comment = " 行政相对人代码_3（组织机构代码）组织机构代码 ")
    private String XINGZHENGXIANGDUIRDM_3ZZJGDM;
    @EasySmartField(value = "tax_registration_number", type = "VARCHAR2(15)", comment = " 行政相对人代码_4（税务登记号）税务登记号 ")
    private String XINGZHENGXIANGDUIRDM_4SWDJH;
    @EasySmartField(value = "institution_certificate_no", type = "VARCHAR2(12)", comment = " 行政相对人代码_5（事业单位证书号）事业单位证书号 ")
    private String XINGZHENGXIANGDUIRDM_5SYDWZSH;
    @EasySmartField(value = "social_organization_registration_certificate_number", type = "VARCHAR2(50)", comment = " 行政相对人代码_6（社会组织登记证号）社会组织登记证号 ")
    private String XINGZHENGXIANGDUIRDM_6SHZZDJZH;
    @EasySmartField(value = "legal_representative", type = "VARCHAR2(50)", comment = " 法定代表人 ")
    private String FADINGDAIBIAOR;
    @EasySmartField(value = "legal_representative_id_card", type = "VARCHAR2(18)", comment = " 法定代表人身份证号 ")
    private String FADINGDAIBIAORSFZH;
    @EasySmartField(value = "document_type", type = "VARCHAR2(64)", comment = " 证件类型 ")
    private String ZHENGJIANLEIXING;
    @EasySmartField(value = "identification_number", type = "VARCHAR2(64)", comment = " 证件号码 ")
    private String ZHENGJIANHAOMA;
    @EasySmartField(value = "administrative_penalty_decision_document_num", type = "VARCHAR2(128)", comment = " 行政处罚决定书文号 ")
    private String XINGZHENGCHUFAJDSWH;
    @EasySmartField(value = "illegal_acts_types", type = "VARCHAR2(2000)", comment = " 违法行为类型 ")
    private String WEIFAXINGWEILX;
    @EasySmartField(value = "illegal_facts", type = "VARCHAR2(2000)", comment = " 违法事实 ")
    private String WEIFASHISHI;
    @EasySmartField(value = "punishment_basis", type = "VARCHAR2(2048)", comment = " 处罚依据 ")
    private String CHUFAYIJU;
    @EasySmartField(value = "punishment_category", type = "VARCHAR2(255)", comment = " 处罚类别 ")
    private String CHUFALEIBIE;
    @EasySmartField(value = "punishment_content", type = "VARCHAR2(4000)", comment = " 处罚内容 ")
    private String CHUFANEIRONG;
    @EasySmartField(value = "penalty_amount", type = "NUMBER(14)", comment = " 处罚金额（万元）单位：万元  ")
    private String CHUFAJINEWY;
    @EasySmartField(value = "confiscation_illegal_income_gains", type = "NUMBER(14)", comment = " 没收违法所得、没收非法财务的金额（万元）单位：万元  ")
    private String MEISHOUWEIFASDMSFFCWDJEWY;
    @EasySmartField(value = "revocation_license_num", type = "VARCHAR2(200)", comment = " 暂扣或吊销证照名称及编号 ")
    private String ZANKOUHUODIAOXZZMCJBH;
    @EasySmartField(value = "punishment_decision_date", type = "DATE", comment = " 处罚决定日期 ")
    private String CHUFAJUEDINGRQ;
    @EasySmartField(value = "punishment_validity_period", type = "DATE", comment = " 处罚有效期 ")
    private String CHUFAYOUXIAOQ;
    @EasySmartField(value = "publicity_deadline", type = "DATE", comment = " 公示截止期 ")
    private String GONGSHIJIEZHIQ;
    @EasySmartField(value = "penalty_authorities", type = "VARCHAR2(255)", comment = " 处罚机关 ")
    private String CHUFAJIGUAN;
    @EasySmartField(value = "punishment_organs_unified_social_credit_code", type = "VARCHAR2(18)", comment = " 处罚机关统一社会信用代码 ")
    private String CHUFAJIGUANTYSHXYDM;
    @EasySmartField(value = "data_source_unit", type = "VARCHAR2(200)", comment = " 数据来源单位 ")
    private String SHUJULAIYUANDW;
    @EasySmartField(value = "data_source_unit_unified_social_credit_code", type = "VARCHAR2(18)", comment = " 数据来源单位统一社会信用代码 ")
    private String SHUJULAIYUANDWTYSHXYDM;
    @EasySmartField(value = "remarks", type = "VARCHAR2(512)", comment = " 备注 ")
    private String BEIZHU;
    @EasySmartField(value = "data_export_time", type = "DATE", comment = " 数据导出时间 ")
    private String SHUJUDAOCHUSJ;
    @EasySmartField(value = "begin_time", type = "VARCHAR2(200)", comment = "  ")
    private String BEGINTIME;
}
