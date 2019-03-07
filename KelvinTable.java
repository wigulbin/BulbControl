package com.augment.golden.bulbcontrol;

import android.graphics.Color;
import android.util.SparseArray;

public class KelvinTable {

    public static String getRGB(int kelvin){
        String rgb = kelvinColorArr.get(kelvin);
        if(rgb != null) {
            String[] rgbArr = rgb.split(":");

            String hex = String.format("#%02x%02x%02x", Integer.parseInt(rgbArr[0]), Integer.parseInt(rgbArr[1]), Integer.parseInt(rgbArr[2]));
            int red = (Integer.parseInt(rgbArr[0]) << 16) & 0x00FF0000;
            int green = (Integer.parseInt(rgbArr[1]) << 8) & 0x00FF0000;
            int blue = (Integer.parseInt(rgbArr[1])) & 0xFF000000;

//            return 0xFF000000 | red | green | blue;
            return hex;
        }

        return "";
    }

    private static SparseArray<String> kelvinColorArr = new SparseArray<>();
    static{
//        kelvinColorArr.put(1000, "255:56:0");
//        kelvinColorArr.put(1100, "255:71:0");
//        kelvinColorArr.put(1200, "255:83:0");
//        kelvinColorArr.put(1300, "255:93:0");
//        kelvinColorArr.put(1400, "255:101:0");
//        kelvinColorArr.put(1500, "255:109:0");
//        kelvinColorArr.put(1600, "255:115:0");
//        kelvinColorArr.put(1700, "255:121:0");
//        kelvinColorArr.put(1800, "255:126:0");
//        kelvinColorArr.put(1900, "255:131:0");
        kelvinColorArr.put(2000, "255:138:18");
        kelvinColorArr.put(2100, "255:142:33");
        kelvinColorArr.put(2200, "255:147:44");
        kelvinColorArr.put(2300, "255:152:54");
        kelvinColorArr.put(2400, "255:157:63");
        kelvinColorArr.put(2500, "255:161:72");
        kelvinColorArr.put(2600, "255:165:79");
        kelvinColorArr.put(2700, "255:169:87");
        kelvinColorArr.put(2800, "255:173:94");
        kelvinColorArr.put(2900, "255:177:101");
        kelvinColorArr.put(3000, "255:180:107");
        kelvinColorArr.put(3100, "255:184:114");
        kelvinColorArr.put(3200, "255:187:120");
        kelvinColorArr.put(3300, "255:190:126");
        kelvinColorArr.put(3400, "255:193:132");
        kelvinColorArr.put(3500, "255:196:137");
        kelvinColorArr.put(3600, "255:199:143");
        kelvinColorArr.put(3700, "255:201:148");
        kelvinColorArr.put(3800, "255:204:153");
        kelvinColorArr.put(3900, "255:206:159");
        kelvinColorArr.put(4000, "255:209:163");
        kelvinColorArr.put(4100, "255:211:168");
        kelvinColorArr.put(4200, "255:213:173");
        kelvinColorArr.put(4300, "255:215:177");
        kelvinColorArr.put(4400, "255:217:182");
        kelvinColorArr.put(4500, "255:219:186");
        kelvinColorArr.put(4600, "255:221:190");
        kelvinColorArr.put(4700, "255:223:194");
        kelvinColorArr.put(4800, "255:225:198");
        kelvinColorArr.put(4900, "255:227:202");
        kelvinColorArr.put(5000, "255:228:206");
        kelvinColorArr.put(5100, "255:230:210");
        kelvinColorArr.put(5200, "255:232:213");
        kelvinColorArr.put(5300, "255:233:217");
        kelvinColorArr.put(5400, "255:235:220");
        kelvinColorArr.put(5500, "255:236:224");
        kelvinColorArr.put(5600, "255:238:227");
        kelvinColorArr.put(5700, "255:239:230");
        kelvinColorArr.put(5800, "255:240:233");
        kelvinColorArr.put(5900, "255:242:236");
        kelvinColorArr.put(6000, "255:243:239");
        kelvinColorArr.put(6100, "255:244:242");
        kelvinColorArr.put(6200, "255:245:245");
        kelvinColorArr.put(6300, "255:246:247");
        kelvinColorArr.put(6400, "255:248:251");
        kelvinColorArr.put(6500, "255:249:253");
        kelvinColorArr.put(6600, "254:249:255");
        kelvinColorArr.put(6700, "252:247:255");
        kelvinColorArr.put(6800, "249:246:255");
        kelvinColorArr.put(6900, "247:245:255");
        kelvinColorArr.put(7000, "245:243:255");
        kelvinColorArr.put(7100, "243:242:255");
        kelvinColorArr.put(7200, "240:241:255");
        kelvinColorArr.put(7300, "239:240:255");
        kelvinColorArr.put(7400, "237:239:255");
        kelvinColorArr.put(7500, "235:238:255");
        kelvinColorArr.put(7600, "233:237:255");
        kelvinColorArr.put(7700, "231:236:255");
        kelvinColorArr.put(7800, "230:235:255");
        kelvinColorArr.put(7900, "228:234:255");
        kelvinColorArr.put(8000, "227:233:255");
        kelvinColorArr.put(8100, "225:232:255");
        kelvinColorArr.put(8200, "224:231:255");
        kelvinColorArr.put(8300, "222:230:255");
        kelvinColorArr.put(8400, "221:230:255");
        kelvinColorArr.put(8500, "220:229:255");
        kelvinColorArr.put(8600, "218:229:255");
        kelvinColorArr.put(8700, "217:227:255");
        kelvinColorArr.put(8800, "216:227:255");
        kelvinColorArr.put(8900, "215:226:255");
        kelvinColorArr.put(9000, "214:225:255");
//        kelvinColorArr.put(9100, "212:225:255");
//        kelvinColorArr.put(9200, "211:224:255");
//        kelvinColorArr.put(9300, "210:223:255");
//        kelvinColorArr.put(9400, "209:223:255");
//        kelvinColorArr.put(9500, "208:222:255");
//        kelvinColorArr.put(9600, "207:221:255");
//        kelvinColorArr.put(9700, "207:221:255");
//        kelvinColorArr.put(9800, "206:220:255");
//        kelvinColorArr.put(9900, "205:220:255");
//        kelvinColorArr.put(10000, "207:218:255");
//        kelvinColorArr.put(10100, "207:218:255");
//        kelvinColorArr.put(10200, "206:217:255");
//        kelvinColorArr.put(10300, "205:217:255");
//        kelvinColorArr.put(10400, "204:216:255");
//        kelvinColorArr.put(10500, "204:216:255");
//        kelvinColorArr.put(10600, "203:215:255");
//        kelvinColorArr.put(10700, "202:215:255");
//        kelvinColorArr.put(10800, "202:214:255");
//        kelvinColorArr.put(10900, "201:214:255");
//        kelvinColorArr.put(11000, "200:213:255");
//        kelvinColorArr.put(11100, "200:213:255");
//        kelvinColorArr.put(11200, "199:212:255");
//        kelvinColorArr.put(11300, "198:212:255");
//        kelvinColorArr.put(11400, "198:212:255");
//        kelvinColorArr.put(11500, "197:211:255");
//        kelvinColorArr.put(11600, "197:211:255");
//        kelvinColorArr.put(11700, "197:210:255");
//        kelvinColorArr.put(11800, "196:210:255");
//        kelvinColorArr.put(11900, "195:210:255");
//        kelvinColorArr.put(12000, "195:209:255");
    }
}
