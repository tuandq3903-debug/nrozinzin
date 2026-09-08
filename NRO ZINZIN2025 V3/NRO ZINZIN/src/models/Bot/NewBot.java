/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.Bot;

import java.util.Random;
import models.Template;
import server.Load_Database;


/**
 *
 * @author Administrator
 */
public class NewBot {
    public static NewBot i;
    
    public boolean LOAD_PART = true;
    public int MAXPART = 0;
    public static int[][] PARTBOT = new int[Load_Database.ITEM_TEMPLATES.size()][4];
    

    private final String[] FULL_NAMES = {
"hung123", "16042001", "mamama", "21428115", "prono1", "abcdff", "ductaidz",
        "bap11", "118118", "meme", "nnttrun", "tranthaikiet", "nhaxinhh", "No0", "Boss", "admin2k5", "Hasaki",
        "02102004", "hungpro2", "vip888", "vipsr", "Sharen", "hungdzok", "trumsv", "vuductam", "Saoblue",
        "910209", "tranphu1611", "hihih1", "salada", "baocony", "baocony1", "baocony2", "nam6821", "nam6822",
        "duytrumlh", "trumkame", "0nelove", "hghk123", "Datpzm", "anhkt111", "272005", "272007", "namvip",
        "tientien", "kiemmatb92", "nminhtruc1", "bidat27", "vip111", "huyyy", "nhocxone", "traidatz", "full1",
        "iugiang", "cuongcon", "Nrocuto", "cccccccc", "Huy19012007", "Tuananh12", "kenzz", "tinhdz9112", "flyg5",
        "anhkoyeu", "pocollo34", "hoangvanhung", "Xuanhuypro", "Xuanhuypro01", "dmadlon", "heloskai", "5078092157",
        "dinhbang", "fifai", "ahung", "broly", "haclong", "Heo", "concachabac", "nmvipvc",
        "ngocxuan", "ngocxuan1", "crazy1", "anhthanhdz", "daika12a", "phivip", "zinos", "nguyenvipne", "gauyeu",
        "1172003", "maideptrai", "huybg206", "laithanhdiep", "hthaidz", "phikovip", "teoteocu", "Khangcuto", "xxxxxx",
        "acccc0", "ruancc", "bo11009", "testnro001", "shinkk", "vanduc16ckk", "quandz88", "quocanh2008", "timogacon",
        "quaypham", "Milksiv", "kiet", "severao", "choiemdianh", "trungpvc1998", "Vy", "tiktok", "quycute",
        "cuongconlc", "Nhovk123", "Haductho", "Haductho1", "Haductho5", "hauductho9", "vudz1234", "trongluan",
        "bacdzkt2003", "bac123", "longtd", "viet345", "doxuantruong", "shiroboyy", "admin", "0344577",
        "chinhmac2001", "Anhdacf1", "vunhuc4", "090901", "phuthien02082004", "10102004", "090900", "ShakiVN",
        "quocv754", "shen", "Naboleon2023", "vunhuc5", "thanhkcr1", "Quyhidan", "tam123", "lqlqlqlq", "hungkuu",
        "hauhyhung24", "duongbe", "Buivt123", "Hongvu1234", "huutai", "kahaga", "quocdatmmo", "tuandz123", "kahaga1",
        "nopro0xz", "nopro9xz", "hyperxz", "chipu", "Quanglee", "phuongaz", "vudz12344", "nrotest", "anhdz00",
        "chithanh06", "Thang1504", "nhatkenbn", "lincute", "ngqcthg", "onezion", "binga", "tqt11052005", "vietcombank",
        "xengdubai", "nam1818", "Trunghau", "thangzi2305", "lequan1809", "giavan123", "hoapro19002", "lamvlog",
        "vipppp", "d", "dat555", "Vailonluon", "Vailonluon1", "dat5555", "Dohuy2004", "anhza", "Hoangdztt",
        "Hoang1", "test1game", "Xuanthanh06", "Minhdang", "chienco2909", "trungpvc19988", "xuanduy", "chubodoi2024",
        "ciudubai", "tretrau", "dinhphi05", "260898", "lampcnro", "pmnhat2", "sexkoche", "nminhtruc2",
        "sieuhoa", "phongkk", "Xuanhuypro02", "Xuanhuypro03", "Xuanhuypro04", "Xuanhuypro05", "Kaiosima", "hoagnguu",
        "crushmylove", "101002", "tuan1711", "anhem", "anhem1", "nan123", "cuongahihi", "thaiqwe", "510203",
        "kazama", "kazama1", "kazama2", "anhviet130", "Hau455", "090601", "quanghuy", "ss1calick", "ss2calick",
        "ss3calick", "ss1calicks", "090902", "090903", "chubedanka", "admin1", "dragonball", "onehit09", "Echolocation",
        "bighero", "hungpro3", "1234512112", "123451213", "Dinoooo", "quocanh2009", "vanbac123", "Naboleon2022",
        "Nghiabeo2002", "trdip", "daimao", "top1km", "Tientdk11", "140923", "phhao", "1409203", "anhemm",
        "nminhtruc3", "Nghia2002", "161123", "07022007", "yamate2", "taipro", "18th2", "top1km1", "top1km2",
        "bangtayto", "chienco2004", "chienco2005", "gobuoip", "huudark1", "vudz12345", "vinhh", "z",
        "ken", "vptcd2", "thua123kg", "18th02", "oggyx00", "qeishhsjs", "bevis", "Th100102", "Namdzno1st",
        "zin123", "dohuy2003", "Nghiapro", "busnake2020", "vivanduy", "nro1", "nro2", "nro3", "096204", "096131",
        "dp2007", "kappi000", "hungpro4", "hungpro5", "admin7", "admin8", "quan", "admin9", "Buithomanh",
        "quochuy", "036999", "kietok", "anhne", "thanh0169", "taanhquoc", "dohoangan1", "cfz1k", "abc113", "ad",
        "phucle", "ahtu8", "bigheroo", "nminhtruc4", "tetsuya", "Taolathanh", "Lephu", "Khang115", "buithomanh369",
        "nrosv6y", "Minh1105", "quocanh2004", "kakalot34", "buithomanh1", "hvt2k9", "slphu123", "viet2007", "11234",
        "adqc", "adqcgame", "phuc882k2", "01", "chubedan", "Sp",
        // Dữ liệu mới được thêm
        "longpro99", "minhvip2k", "kietdragon", "thanhz123", "duy2k5", "trungboss", "namx10", "huygoku",
        "baoshadow", "quocno1", "tamcute88", "vietproz", "hungkame", "anvip2023", "datz777", "linhstorm",
        "cuongpvp", "traiking9", "phongdz11", "tuanblue2", "khanhvippro", "dung2k7", "hoangshin",
        "binhpro123", "thienx99", "giapvp2024", "phucdark", "quangz10", "haiblue88", "sonvipz",
        "trinhnoob7", "khoaprox", "lamdz555", "thaikingz", "vux123", "hieudzpro", "chinh2k9",
        "anhpvp88", "duongxpro", "tinhshadow", "namkame99", "hungz2023", "baovipx", "quocstorm7",
        "tampro2k", "vietdz123", "linhbluez", "cuongno1st", "traipvp88", "phongkingz"
    };


    
    
    public static NewBot gI(){
         if(i == null){
             i = new NewBot();
         }
         return i;
    }
    
  public void LoadPart() {
    if (LOAD_PART) {
        int i = 0;
        for (Template.ItemTemplate it : Load_Database.ITEM_TEMPLATES) {
            if (it.type == 5) {
                if (it.head != -1 && it.leg != -1 && it.body != -1 && it.leg != 194) {
                    // Gán giá trị theo giới tính
                    if (it.gender == 0 || it.gender == 2) {
                        PARTBOT[i][0] = 383; // head
                        PARTBOT[i][1] = 385; // leg
                        PARTBOT[i][2] = 384; // body
                    } else { // it.gender == 1
                        PARTBOT[i][0] = 391; // head
                        PARTBOT[i][1] = 393; // leg
                        PARTBOT[i][2] = 392; // body
                    }
                    PARTBOT[i][3] = it.gender; // Lưu giới tính
                    i++;
                    MAXPART++;
                }
            }
        }
        LOAD_PART = false;
    }
}

   
 
    public String Getname() {
        Random rand = new Random();
        return FULL_NAMES[rand.nextInt(FULL_NAMES.length)];
    }
   
   
   public int getIndex(int gender){
        int Random = new Random().nextInt(MAXPART);
        int gend = PARTBOT[Random][3];
     //   if(gend == gender || gend == 3){
        if(gend == gender){
            return Random;
        } else {
            return getIndex(gender);
        }
   }
  
   
   public void runBot(int type , ShopBot shop , int slot){
          LoadPart();
            for(int i = 0; i < slot ; i++){
               int Gender = new Random().nextInt(3);
               int Random1 = getIndex(Gender);
               int head = PARTBOT[Random1][0];
               int leg = PARTBOT[Random1][1];
               int body = PARTBOT[Random1][2];
               if (shop != null){
                   shop = new ShopBot(shop);
               }
               int flag = Load_Database.FLAGS_BAGS.get(new Random().nextInt(Load_Database.FLAGS_BAGS.size())).id;
               Bot b = new Bot((short) head ,(short) body ,(short) leg , type , Getname() , shop , (short) flag);
               Sanb bos = new Sanb(b);
               Mobb mo1 = new Mobb(b);
               b.mo1 = mo1;
               b.boss = bos;
               int congThem = new Random().nextInt(50000000);
               b.nPoint.limitPower = 8;
               b.nPoint.power = 1000 + congThem;
               b.nPoint.tiemNang = 20000000 + congThem;
               b.nPoint.dameg = 10;
               b.nPoint.mpg = 200000;
               b.nPoint.mpMax = 200000;
               b.nPoint.mp = 200000;
               b.nPoint.hpg = 10000;
               b.nPoint.hpMax = 10000;
               b.nPoint.hp = 10000;
               b.nPoint.maxStamina = 20000;
               b.nPoint.stamina = 20000;
               b.nPoint.critg = 10;
               b.nPoint.defg = 10;
               b.gender = (byte) Gender;
               b.leakSkill();
               b.joinMap();
               if(shop != null){
                   shop.bot = b;
               }
               if(b != null){
               BotManager.gI().bot.add(b);
          }
      }
   }
}