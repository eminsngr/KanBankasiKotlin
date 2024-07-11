package com.example.bloodapp.utils

object AddressUtils {
    private val divisions = listOf(
        "Select City",
        "Adana",
        "Ankara",
        "Istanbul",
        "Izmir",
        "Antalya",
        "Bursa",
        "Gaziantep",
        "Konya",
        "Mersin"
        // Diğer illeri buraya ekleyin
    )

    private val districts = hashMapOf(
        divisions[0] to listOf("Select District"),
        divisions[1] to listOf(
            "Select District",
            "Seyhan",
            "Çukurova",
            "Yüreğir",
            "Sarıçam",
            "Karaisalı",
            "Kozan",
            "Ceyhan"
        ),
        divisions[2] to listOf(
            "Select District",
            "Çankaya",
            "Yenimahalle",
            "Keçiören",
            "Mamak",
            "Etimesgut",
            "Sincan",
            "Altındağ",
            "Gölbaşı",
            "Pursaklar",
            "Polatlı"
        ),
        divisions[3] to listOf(
            "Select District",
            "Fatih",
            "Beşiktaş",
            "Kadıköy",
            "Üsküdar",
            "Şişli",
            "Beyoğlu",
            "Bakırköy",
            "Sarıyer",
            "Ataşehir",
            "Eyüpsultan"
        ),
        divisions[4] to listOf(
            "Select District",
            "Konak",
            "Karşıyaka",
            "Bornova",
            "Buca",
            "Bayraklı",
            "Çiğli",
            "Gaziemir",
            "Balçova",
            "Narlıdere",
            "Güzelbahçe"
        ),
        divisions[5] to listOf(
            "Select District",
            "Muratpaşa",
            "Kepez",
            "Konyaaltı",
            "Alanya",
            "Manavgat",
            "Serik",
            "Aksu",
            "Döşemealtı",
            "Kumluca",
            "Kaş"
        ),
        divisions[6] to listOf(
            "Select District",
            "Osmangazi",
            "Yıldırım",
            "Nilüfer",
            "İnegöl",
            "Gemlik",
            "Gürsu",
            "Kestel",
            "Karacabey",
            "Mustafakemalpaşa",
            "Orhangazi"
        ),
        divisions[7] to listOf(
            "Select District",
            "Şahinbey",
            "Şehitkamil",
            "Nizip",
            "Oğuzeli",
            "Islahiye",
            "Karkamış",
            "Araban",
            "Yavuzeli",
            "Nurdağı"
        ),
        divisions[8] to listOf(
            "Select District",
            "Selçuklu",
            "Meram",
            "Karatay",
            "Akşehir",
            "Beyşehir",
            "Ereğli",
            "Ilgın",
            "Seydişehir",
            "Çumra",
            "Cihanbeyli"
        ),
        divisions[9] to listOf(
            "Select District",
            "Akdeniz",
            "Yenişehir",
            "Toroslar",
            "Mezitli",
            "Tarsus",
            "Erdemli",
            "Silifke",
            "Mut",
            "Anamur",
            "Gülnar"
        ),
    )

    private val thanas = hashMapOf(
        "Seyhan" to listOf("Select Neighborhood", "Döşeme", "Pınar", "Yeşilyurt"),
        "Çukurova" to listOf("Select Neighborhood", "Beyazevler", "Huzurevleri", "Yurt"),
        "Yüreğir" to listOf("Select Neighborhood", "Kiremithane", "Serinevler", "Yamaçlı"),
        "Sarıçam" to listOf("Select Neighborhood", "Orhangazi", "İncirlik", "Yeni"),
        "Karaisalı" to listOf("Select Neighborhood", "Büyükdikili", "Küçükdikili", "Altınova"),
        "Kozan" to listOf("Select Neighborhood", "Varsaklar", "Türkeli", "Tufanpaşa"),
        "Ceyhan" to listOf("Select Neighborhood", "Muradiye", "Mustafabeyli", "Büyükmangıt"),

        "Çankaya" to listOf("Select Neighborhood", "Kızılay", "Bahçelievler", "Çukurambar"),
        "Yenimahalle" to listOf("Select Neighborhood", "Batıkent", "Demetevler", "Çiğdemtepe"),
        "Keçiören" to listOf("Select Neighborhood", "Etlik", "Aşağı Eğlence", "Osmangazi"),
        "Mamak" to listOf("Select Neighborhood", "Abidinpaşa", "Durali Alıç", "Mutlu"),
        "Etimesgut" to listOf("Select Neighborhood", "Elvankent", "Eryaman", "Bağlıca"),
        "Sincan" to listOf("Select Neighborhood", "Fatih", "Plevne", "Yenikent"),
        "Altındağ" to listOf("Select Neighborhood", "Ulus", "Doğantepe", "Aydınlıkevler"),
        "Gölbaşı" to listOf("Select Neighborhood", "Bahçelievler", "Karşıyaka", "Oğulbey"),
        "Pursaklar" to listOf("Select Neighborhood", "Saray", "Mimar Sinan", "Altınova"),
        "Polatlı" to listOf("Select Neighborhood", "Şehitlik", "İstiklal", "Çarşı"),

        "Fatih" to listOf("Select Neighborhood", "Sultanahmet", "Aksaray", "Eminönü"),
        "Beşiktaş" to listOf("Select Neighborhood", "Levent", "Ortaköy", "Etiler"),
        "Kadıköy" to listOf("Select Neighborhood", "Moda", "Bostancı", "Suadiye"),
        "Üsküdar" to listOf("Select Neighborhood", "Altunizade", "Çengelköy", "Kuzguncuk"),
        "Şişli" to listOf("Select Neighborhood", "Nişantaşı", "Mecidiyeköy", "Feriköy"),
        "Beyoğlu" to listOf("Select Neighborhood", "Taksim", "Galata", "Karaköy"),
        "Bakırköy" to listOf("Select Neighborhood", "Yeşilköy", "Ataköy", "Zeytinlik"),
        "Sarıyer" to listOf("Select Neighborhood", "Maslak", "Tarabya", "Rumeli Hisarı"),
        "Ataşehir" to listOf("Select Neighborhood", "Barbaros", "İçerenköy", "Kayışdağı"),
        "Eyüpsultan" to listOf("Select Neighborhood", "Eyüp", "Göktürk", "Alibeyköy"),

        "Konak" to listOf("Select Neighborhood", "Alsancak", "Güzelyalı", "Hatay"),
        "Karşıyaka" to listOf("Select Neighborhood", "Bostanlı", "Mavişehir", "Alaybey"),
        "Bornova" to listOf("Select Neighborhood", "Kazım Dirik", "Erzene", "İnönü"),
        "Buca" to listOf("Select Neighborhood", "Şirinyer", "Çamlıkule", "Yıldız"),
        "Bayraklı" to listOf("Select Neighborhood", "Manavkuyu", "Onur", "Adalet"),
        "Çiğli" to listOf("Select Neighborhood", "Ataşehir", "Balatçık", "Kaklıç"),
        "Gaziemir" to listOf("Select Neighborhood", "Akçay", "Beyazevler", "Gazi"),
        "Balçova" to listOf("Select Neighborhood", "Teleferik", "Korutürk", "Çetin Emeç"),
        "Narlıdere" to listOf("Select Neighborhood", "Narbel", "Altıevler", "Çamtepe"),
        "Güzelbahçe" to listOf("Select Neighborhood", "Yaka", "Kahramandere", "Yelki"),

        "Muratpaşa" to listOf("Select Neighborhood", "Lara", "Meltem", "Soğuksu"),
        "Kepez" to listOf("Select Neighborhood", "Gülveren", "Şafak", "Teomanpaşa"),
        "Konyaaltı" to listOf("Select Neighborhood", "Hurma", "Liman", "Molla Yusuf"),
        "Alanya" to listOf("Select Neighborhood", "Mahmutlar", "Avsallar", "Oba"),
        "Manavgat" to listOf("Select Neighborhood", "Side", "Kızılağaç", "Titreyengöl"),
        "Serik" to listOf("Select Neighborhood", "Belek", "Kadriye", "Boğazkent"),
        "Aksu" to listOf("Select Neighborhood", "Kemerağzı", "Altıntaş", "Çalkaya"),
        "Döşemealtı" to listOf("Select Neighborhood", "Yeşilbayır", "Altınkale", "Aşağıoba"),
        "Kumluca" to listOf("Select Neighborhood", "Mavikent", "Adrasan", "Olimpos"),
        "Kaş" to listOf("Select Neighborhood", "Kalkan", "Patara", "Gökseki"),

        "Osmangazi" to listOf("Select Neighborhood", "Çekirge", "Soğanlı", "Dikkaldırım"),
        "Yıldırım" to listOf("Select Neighborhood", "Bağlaraltı", "Hacivat", "Ertuğrulgazi"),
        "Nilüfer" to listOf("Select Neighborhood", "Görükle", "Karaman", "İhsaniye"),
        "İnegöl" to listOf("Select Neighborhood", "Yenice", "Alanyurt", "Süleymaniye"),
        "Gemlik" to listOf("Select Neighborhood", "Umurbey", "Kurşunlu", "Kumla"),
        "Gürsu" to listOf("Select Neighborhood", "Zafer", "Adaköy", "Kurtuluş"),
        "Kestel" to listOf("Select Neighborhood", "Barakfakih", "Gölbaşı", "Vani Mehmet"),
        "Karacabey" to listOf("Select Neighborhood", "Hamidiye", "Hüdavendigar", "Sultaniye"),
        "Mustafakemalpaşa" to listOf("Select Neighborhood", "Fevzi Paşa", "Lalaşahin", "Hamzabey"),
        "Orhangazi" to listOf("Select Neighborhood", "Fatih", "Arapzade", "Karsak"),

        "Şahinbey" to listOf("Select Neighborhood", "Binevler", "Kolejtepe", "Üçgöze"),
        "Şehitkamil" to listOf("Select Neighborhood", "Batıkent", "Belkız", "Emek"),
        "Nizip" to listOf("Select Neighborhood", "Karkamış", "Kocatepe", "Şahinbey"),
        "Oğuzeli" to listOf("Select Neighborhood", "Hürriyet", "Bahçelievler", "Köprübaşı"),
        "Islahiye" to listOf("Select Neighborhood", "Yeni", "Çarşı", "Bağlarbaşı"),
        "Karkamış" to listOf("Select Neighborhood", "Orta", "Barbaros", "Camiikebir"),
        "Araban" to listOf("Select Neighborhood", "Yeni", "Kırık", "Çaybaşı"),
        "Yavuzeli" to listOf("Select Neighborhood", "Yeni", "Çarşı", "Başpınar"),
        "Nurdağı" to listOf("Select Neighborhood", "Yeni", "Atatürk", "İstiklal"),

        "Selçuklu" to listOf("Select Neighborhood", "Bosna Hersek", "Sille", "Kosova"),
        "Meram" to listOf("Select Neighborhood", "Aksinne", "Alavardı", "Armağan"),
        "Karatay" to listOf("Select Neighborhood", "İstiklal", "Keykubat", "Fevzi Çakmak"),
        "Akşehir" to listOf("Select Neighborhood", "Gazi", "İstasyon", "Sarı"),
        "Beyşehir" to listOf("Select Neighborhood", "Evsat", "Esentepe", "Bağlar"),
        "Ereğli" to listOf("Select Neighborhood", "Çömlekçi", "Hüyük", "Karaçay"),
        "Ilgın" to listOf("Select Neighborhood", "Yıldız", "Mavi", "Cumhuriyet"),
        "Seydişehir" to listOf("Select Neighborhood", "Kocatepe", "Hacıseyitali", "Yeniköy"),
        "Çumra" to listOf("Select Neighborhood", "Merkez", "Dörtyol", "Karahüyük"),
        "Cihanbeyli" to listOf("Select Neighborhood", "Zafer", "Fatih", "Alparslan"),

        "Akdeniz" to listOf("Select Neighborhood", "Hal", "Nusratiye", "İhsaniye"),
        "Yenişehir" to listOf("Select Neighborhood", "Menteş", "Barbaros", "Huzurkent"),
        "Toroslar" to listOf("Select Neighborhood", "Toros", "Sağlık", "Arpaçsakarlar"),
        "Mezitli" to listOf("Select Neighborhood", "Akdeniz", "Viranşehir", "Tece"),
        "Tarsus" to listOf("Select Neighborhood", "Şahin", "Bağlar", "Kavaklı"),
        "Erdemli" to listOf("Select Neighborhood", "Akdeniz", "Arpaçbahşiş", "Kargıpınarı"),
        "Silifke" to listOf("Select Neighborhood", "Saray", "Göksu", "Yeni"),
        "Mut" to listOf("Select Neighborhood", "Keleşpınar", "Evren", "Kürkçü"),
        "Anamur" to listOf("Select Neighborhood", "Bahçelievler", "Saray", "Yeşilyurt"),
        "Gülnar" to listOf("Select Neighborhood", "Fatih", "Barbaros", "Cumhuriyet")
    )


    fun getDivisions(): List<String> = divisions
    fun getDistrict(division: String): List<String> = districts.get(division) ?: listOf()

    fun getThan(district: String): List<String> = thanas[district] ?: listOf()
}