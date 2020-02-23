package applyextra.operations.model;

import lombok.Getter;
import nl.ing.sc.arrangementretrieval.listarrangements2.value.RoleWithArrangement;

public enum PackageType {
    /**
     (1, 2, 3, 4) Integers matching return values of InquirePackageAgreement, DO NOT CHANGE!!
     (140, 141. 142, 143) Integers matching return values of gArrangments
     */
    BETAALPAKKET("BetaalPakket", 2, "140"),
    ROYAALPAKKET("RoyaalPakket", 3, "141"),
    BASISPAKKET("BasisPakket", 1, "142"),
    ORANJEPAKKET("OranjePakket", 4, "143");

    @Getter
    private Integer packageType;
    private String packageName;
    @Getter
    private String packageCode;

    PackageType(String packageName, int packageType, String packageCode){
        this.packageName = packageName;
        this.packageType = packageType;
        this.packageCode = packageCode;
    }

        public String toString(){
            return this.packageName;
        }

    /**
     * For response from InquirePackageAgreement
     * @param packageType Can have values 1, 2, 3, 4
     * @return
     */
    public static PackageType byInteger(Integer packageType){
        for(PackageType type : PackageType.values()){
            if(type.packageType.equals(packageType)){
                return type;
            }
        }
        return null;
    }

    public static PackageType byString(String packageName) {
        for (PackageType type : PackageType.values()) {
            if (type.packageName.equalsIgnoreCase(packageName)) {
                return type;
            }
        }
        return null;
    }

    /**
     * For response from gArrangements
     * @param packageCode can have values 140, 141, 142, 143
     * @return
     */
    public static PackageType valueOfPackageCode(String packageCode) {
        for (PackageType type : PackageType.values()) {
            if (type.packageCode.equals(packageCode)) {
                return type;
            }
        }
        return null;
    }

    public static boolean isValidPackageType(RoleWithArrangement a) {
        return a.getArrangement().getProduct().equals(143) // OranjePakket
                || a.getArrangement().getProduct().equals(142) // BasisPakket
                || a.getArrangement().getProduct().equals(141) // RoyaalPakket
                || a.getArrangement().getProduct().equals(140);
    }

}
