package com.gerp.usermgmt.model.address;

import java.util.HashMap;
import java.util.Map;

public class ProvinceDistrict {

    public static final String PROVINCEI = "Taplejung,Sankhuwasabha,Solukhumbu,Okhaldhunga,Khotang,Bhojpur,Dhankuta,Terhathum,Panchthar,Ilam,Jhapa,Morang,Sunsari,Udayapur";
    public static final String PROVINCEII = "Saptari,Siraha,Dhanusa,Mahottari,Sarlahi, Rautahat, Bara, Parsa";
    public static final String PROVINCEIII = "Dolakha,Sindhupalchok, Rasuwa,Dhading,Nuwakot,Kathmandu,Bhaktapur,Lalitpur,Kavrepalanchok,Ramechhap,Sindhuli,Makawanpur,Chitawan";
    public static final String PROVINCEIV = " Gorkha,Manang,Mustang,Myagdi,Kaski,Lamjung,Tanahu,Nawalparasi East,Syangja,Parbat,Baglung";
    public static final String PROVINCEV = " Rukum East,Rolpa,Pyuthan,Gulmi,Arghakhanchi,Palpa,Nawalparasi West,Rupandehi,Kapilbastu,Dang,Banke,Bardiya";
    public static final String PROVINCEVI = " Dolpa,Mugu,Humla,Jumla,Kalikot,Dailekh,Jajarkot,Rukum West,Salyan,Surkhet";
    public static final String PROVINCEVII = " Bajura,Bajhang,Darchula,Baitadi,Dadeldhura,Doti,Achham,Kailali,Kanchanpur";

    public static Map<String , String> data = new HashMap<>();
    static {
        data.put("province 1", PROVINCEI);
        data.put("province 2", PROVINCEII);
        data.put("province 3", PROVINCEIII);
        data.put("province 4", PROVINCEIV);
        data.put("province 5", PROVINCEV);
        data.put("province 6", PROVINCEVI);
        data.put("province 7", PROVINCEVII);
    }

   public String getProvinceFromDistrict(String district) {
        String province = null;
       for (Map.Entry<String, String> entry : data.entrySet()) {
          if (entry.getValue().contains(district)) {
              province = entry.getKey();
          }
       }
        return province;
    }
}
