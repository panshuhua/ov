package com.ivay.ivay_common.enums;

/**
 * @EnumName OverDueLevelEnum
 * @Description 逾期级别
 * @Author Ryan
 * @Date 2019/7/9 15:47
 */
public enum OverDueLevelEnum {

    OVERDUE_LEVEL_ONE("M1",31),
    OVERDUE_LEVEL_TWO("M2",61),
    OVERDUE_LEVEL_THREE("M3",91),
    OVERDUE_LEVEL_FOUR("M4",121),
    OVERDUE_LEVEL_FIVE("M5",151),
    OVERDUE_LEVEL_SIX("M6",181),
    OVERDUE_LEVEL_SIX_PLUS("M6+",365*100);

    private String level;
    private Integer day;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    private OverDueLevelEnum(String level, Integer day){
        this.level = level;
        this.day = day;
    }

    /**
     * @Description 根据逾期天数获取逾期等级
     * @Author Ryan
     * @Param [day]
     * @Return java.lang.String
     * @Date 2019/7/9 16:10
     */
    public static String getLevelByDay(Integer day){
        if(day > 0){
            if(day < OVERDUE_LEVEL_ONE.day){
                return OVERDUE_LEVEL_ONE.level;
            }
            if(day < OVERDUE_LEVEL_TWO.day){
                return OVERDUE_LEVEL_TWO.level;
            }
            if(day < OVERDUE_LEVEL_THREE.day){
                return OVERDUE_LEVEL_ONE.level;
            }
            if(day < OVERDUE_LEVEL_FOUR.day){
                return OVERDUE_LEVEL_ONE.level;
            }
            if(day < OVERDUE_LEVEL_FIVE.day){
                return OVERDUE_LEVEL_ONE.level;
            }
            if(day < OVERDUE_LEVEL_SIX.day){
                return OVERDUE_LEVEL_ONE.level;
            }
            if(day < OVERDUE_LEVEL_SIX_PLUS.day){
                return OVERDUE_LEVEL_SIX_PLUS.level;
            }
        }
        return null;
    }

    /**
     * @Description 根据逾期等级获取天数
     * @Author Ryan
     * @Param [level]
     * @Return java.lang.Integer
     * @Date 2019/7/9 16:11
     */
    public static Integer getDayByLevel(String level){
        OverDueLevelEnum[] values = values();
        for(OverDueLevelEnum overDueLevelEnum : values){
            if(overDueLevelEnum.getLevel().equals(level)){
                return overDueLevelEnum.getDay();
            }
        }
        return null;
    }
}
