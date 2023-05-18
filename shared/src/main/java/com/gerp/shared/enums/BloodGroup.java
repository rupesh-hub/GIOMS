package com.gerp.shared.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.pojo.KeyValuePojo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum BloodGroup {
    A_POSITIVE("A_POSITIVE", "A +ve", "A +ve"),
    A_NEGATIVE("A_NEGATIVE", "A -ve", "A -ve"),
    B_POSITIVE("B_POSITIVE", "B +ve", "B +ve"),
    B_NEGATIVE("B_NEGATIVE", "B -ve", "B -ve"),
    O_POSITIVE("O_POSITIVE", "O +ve", "O +ve"),
    O_NEGATIVE("O_NEGATIVE", "O -ve", "O -ve"),
    AB_POSITIVE("AB_POSITIVE", "AB +ve", "AB +ve"),
    AB_NEGATIVE("AB_NEGATIVE", "AB -ve", "AB -ve");
    private String key;
    private String valueNepali;
    private String valueEnglish;

    BloodGroup(String key, String valueNepali, String valueEnglish) {
        this.key = key;
        this.valueNepali = valueNepali;
        this.valueEnglish = valueEnglish;
    }

    public String getKey() {
        return key;
    }

    public String getValueNepali() {
        return valueNepali;
    }

    public String getValueEnglish() {
        return valueEnglish;
    }

    public static List<KeyValuePojo> getBloodGroupList() {
        List<KeyValuePojo> bloodGroupList;
        bloodGroupList = Arrays.stream(BloodGroup.values()).map(bloodGroup ->
                new KeyValuePojo(bloodGroup.key, bloodGroup.valueNepali, bloodGroup.valueEnglish))
                .collect(Collectors.toList());
        return bloodGroupList;
    }

       public static BloodGroup getEnumFromCode(String code){
        switch (code) {

            case "2":
            case "1":
                return BloodGroup.O_POSITIVE;
            case "3":
                return BloodGroup.A_POSITIVE;
            case "4":
                return BloodGroup.A_NEGATIVE;
                case "5":
                return BloodGroup.B_POSITIVE;
            case "6":
                return BloodGroup.B_NEGATIVE;
            case "7":
                return BloodGroup.AB_POSITIVE;
            case "8":
                return BloodGroup.AB_NEGATIVE;
            default:
                return null;

        }
    }

    public static BloodGroup getEnumFromOrdinal(Integer ordinal){
        switch (ordinal) {

            case 0:
                return BloodGroup.A_POSITIVE;
            case 1:
                return BloodGroup.A_NEGATIVE;
            case 2:
                return BloodGroup.B_POSITIVE;
            case 3:
                return BloodGroup.B_NEGATIVE;
            case 4:
                return BloodGroup.O_POSITIVE;
            case 5:
                return BloodGroup.O_NEGATIVE;
            case 6:
                return BloodGroup.AB_POSITIVE;
            case 7:
                return BloodGroup.AB_NEGATIVE;
            default:
                throw new CustomException("Invalid Enum value");
        }
    }


//    public static List<KeyValuePojo> getEnumListWithAll() {
//        return Arrays.stream(Gender.values())
//                .map(x -> KeyValuePojo.builder()
//                        .key(x.toString())
//                        .valueEnglish(x.valueEnglish)
//                        .valueNepali(x.valueNepali)
//                        .build())
//                .collect(Collectors.toList());
//    }
//
//    public static List<KeyValuePojo> getEnumList() {
//        return Arrays.stream(Gender.values())
//                .filter(x->!x.equals(Gender.A))
//                .map(x -> KeyValuePojo.builder()
//                        .key(x.toString())
//                        .valueEnglish(x.valueEnglish)
//                        .valueNepali(x.valueNepali)
//                        .build())
//                .collect(Collectors.toList());
//    }

    public KeyValuePojo getEnum() {
        return KeyValuePojo.builder()
                .key(this.toString())
                .valueEnglish(this.valueEnglish)
                .valueNepali(this.valueNepali)
                .build();
    }

}

