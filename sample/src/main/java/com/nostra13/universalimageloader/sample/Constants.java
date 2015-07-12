/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.universalimageloader.sample;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public final class Constants {

	public static final String[] IMAGES = new String[] {
            // Base64 image
            "data:image/jpeg;base64,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",
			// Heavy images
			"https://lh6.googleusercontent.com/-55osAWw3x0Q/URquUtcFr5I/AAAAAAAAAbs/rWlj1RUKrYI/s1024/A%252520Photographer.jpg",
			"https://lh4.googleusercontent.com/--dq8niRp7W4/URquVgmXvgI/AAAAAAAAAbs/-gnuLQfNnBA/s1024/A%252520Song%252520of%252520Ice%252520and%252520Fire.jpg",
			"https://lh5.googleusercontent.com/-7qZeDtRKFKc/URquWZT1gOI/AAAAAAAAAbs/hqWgteyNXsg/s1024/Another%252520Rockaway%252520Sunset.jpg",
			"https://lh3.googleusercontent.com/--L0Km39l5J8/URquXHGcdNI/AAAAAAAAAbs/3ZrSJNrSomQ/s1024/Antelope%252520Butte.jpg",
			"https://lh6.googleusercontent.com/-8HO-4vIFnlw/URquZnsFgtI/AAAAAAAAAbs/WT8jViTF7vw/s1024/Antelope%252520Hallway.jpg",
			"https://lh4.googleusercontent.com/-WIuWgVcU3Qw/URqubRVcj4I/AAAAAAAAAbs/YvbwgGjwdIQ/s1024/Antelope%252520Walls.jpg",
			"https://lh6.googleusercontent.com/-UBmLbPELvoQ/URqucCdv0kI/AAAAAAAAAbs/IdNhr2VQoQs/s1024/Apre%2525CC%252580s%252520la%252520Pluie.jpg",
			"https://lh3.googleusercontent.com/-s-AFpvgSeew/URquc6dF-JI/AAAAAAAAAbs/Mt3xNGRUd68/s1024/Backlit%252520Cloud.jpg",
			"https://lh5.googleusercontent.com/-bvmif9a9YOQ/URquea3heHI/AAAAAAAAAbs/rcr6wyeQtAo/s1024/Bee%252520and%252520Flower.jpg",
			"https://lh5.googleusercontent.com/-n7mdm7I7FGs/URqueT_BT-I/AAAAAAAAAbs/9MYmXlmpSAo/s1024/Bonzai%252520Rock%252520Sunset.jpg",
			"https://lh6.googleusercontent.com/-4CN4X4t0M1k/URqufPozWzI/AAAAAAAAAbs/8wK41lg1KPs/s1024/Caterpillar.jpg",
			"https://lh3.googleusercontent.com/-rrFnVC8xQEg/URqufdrLBaI/AAAAAAAAAbs/s69WYy_fl1E/s1024/Chess.jpg",
			"https://lh5.googleusercontent.com/-WVpRptWH8Yw/URqugh-QmDI/AAAAAAAAAbs/E-MgBgtlUWU/s1024/Chihuly.jpg",
			"https://lh5.googleusercontent.com/-0BDXkYmckbo/URquhKFW84I/AAAAAAAAAbs/ogQtHCTk2JQ/s1024/Closed%252520Door.jpg",
			"https://lh3.googleusercontent.com/-PyggXXZRykM/URquh-kVvoI/AAAAAAAAAbs/hFtDwhtrHHQ/s1024/Colorado%252520River%252520Sunset.jpg",
			"https://lh3.googleusercontent.com/-ZAs4dNZtALc/URquikvOCWI/AAAAAAAAAbs/DXz4h3dll1Y/s1024/Colors%252520of%252520Autumn.jpg",
			"https://lh4.googleusercontent.com/-GztnWEIiMz8/URqukVCU7bI/AAAAAAAAAbs/jo2Hjv6MZ6M/s1024/Countryside.jpg",
			"https://lh4.googleusercontent.com/-bEg9EZ9QoiM/URquklz3FGI/AAAAAAAAAbs/UUuv8Ac2BaE/s1024/Death%252520Valley%252520-%252520Dunes.jpg",
			"https://lh6.googleusercontent.com/-ijQJ8W68tEE/URqulGkvFEI/AAAAAAAAAbs/zPXvIwi_rFw/s1024/Delicate%252520Arch.jpg",
			"https://lh5.googleusercontent.com/-Oh8mMy2ieng/URqullDwehI/AAAAAAAAAbs/TbdeEfsaIZY/s1024/Despair.jpg",
			"https://lh5.googleusercontent.com/-gl0y4UiAOlk/URqumC_KjBI/AAAAAAAAAbs/PM1eT7dn4oo/s1024/Eagle%252520Fall%252520Sunrise.jpg",
			"https://lh3.googleusercontent.com/-hYYHd2_vXPQ/URqumtJa9eI/AAAAAAAAAbs/wAalXVkbSh0/s1024/Electric%252520Storm.jpg",
			"https://lh5.googleusercontent.com/-PyY_yiyjPTo/URqunUOhHFI/AAAAAAAAAbs/azZoULNuJXc/s1024/False%252520Kiva.jpg",
			"https://lh6.googleusercontent.com/-PYvLVdvXywk/URqunwd8hfI/AAAAAAAAAbs/qiMwgkFvf6I/s1024/Fitzgerald%252520Streaks.jpg",
			"https://lh4.googleusercontent.com/-KIR_UobIIqY/URquoCZ9SlI/AAAAAAAAAbs/Y4d4q8sXu4c/s1024/Foggy%252520Sunset.jpg",
			"https://lh6.googleusercontent.com/-9lzOk_OWZH0/URquoo4xYoI/AAAAAAAAAbs/AwgzHtNVCwU/s1024/Frantic.jpg",
			"https://lh3.googleusercontent.com/-0X3JNaKaz48/URqupH78wpI/AAAAAAAAAbs/lHXxu_zbH8s/s1024/Golden%252520Gate%252520Afternoon.jpg",
			"https://lh6.googleusercontent.com/-95sb5ag7ABc/URqupl95RDI/AAAAAAAAAbs/g73R20iVTRA/s1024/Golden%252520Gate%252520Fog.jpg",
			"https://lh3.googleusercontent.com/-JB9v6rtgHhk/URqup21F-zI/AAAAAAAAAbs/64Fb8qMZWXk/s1024/Golden%252520Grass.jpg",
			"https://lh4.googleusercontent.com/-EIBGfnuLtII/URquqVHwaRI/AAAAAAAAAbs/FA4McV2u8VE/s1024/Grand%252520Teton.jpg",
			"https://lh4.googleusercontent.com/-WoMxZvmN9nY/URquq1v2AoI/AAAAAAAAAbs/grj5uMhL6NA/s1024/Grass%252520Closeup.jpg",
			"https://lh3.googleusercontent.com/-6hZiEHXx64Q/URqurxvNdqI/AAAAAAAAAbs/kWMXM3o5OVI/s1024/Green%252520Grass.jpg",
			"https://lh5.googleusercontent.com/-6LVb9OXtQ60/URquteBFuKI/AAAAAAAAAbs/4F4kRgecwFs/s1024/Hanging%252520Leaf.jpg",
			"https://lh4.googleusercontent.com/-zAvf__52ONk/URqutT_IuxI/AAAAAAAAAbs/D_bcuc0thoU/s1024/Highway%2525201.jpg",
			"https://lh6.googleusercontent.com/-H4SrUg615rA/URquuL27fXI/AAAAAAAAAbs/4aEqJfiMsOU/s1024/Horseshoe%252520Bend%252520Sunset.jpg",
			"https://lh4.googleusercontent.com/-JhFi4fb_Pqw/URquuX-QXbI/AAAAAAAAAbs/IXpYUxuweYM/s1024/Horseshoe%252520Bend.jpg",
			"https://lh5.googleusercontent.com/-UGgssvFRJ7g/URquueyJzGI/AAAAAAAAAbs/yYIBlLT0toM/s1024/Into%252520the%252520Blue.jpg",
			"https://lh3.googleusercontent.com/-CH7KoupI7uI/URquu0FF__I/AAAAAAAAAbs/R7GDmI7v_G0/s1024/Jelly%252520Fish%2525202.jpg",
			"https://lh4.googleusercontent.com/-pwuuw6yhg8U/URquvPxR3FI/AAAAAAAAAbs/VNGk6f-tsGE/s1024/Jelly%252520Fish%2525203.jpg",
			"https://lh5.googleusercontent.com/-GoUQVw1fnFw/URquv6xbC0I/AAAAAAAAAbs/zEUVTQQ43Zc/s1024/Kauai.jpg",
			"https://lh6.googleusercontent.com/-8QdYYQEpYjw/URquwvdh88I/AAAAAAAAAbs/cktDy-ysfHo/s1024/Kyoto%252520Sunset.jpg",
			"https://lh4.googleusercontent.com/-vPeekyDjOE0/URquwzJ28qI/AAAAAAAAAbs/qxcyXULsZrg/s1024/Lake%252520Tahoe%252520Colors.jpg",
			"https://lh4.googleusercontent.com/-xBPxWpD4yxU/URquxWHk8AI/AAAAAAAAAbs/ARDPeDYPiMY/s1024/Lava%252520from%252520the%252520Sky.jpg",
			"https://lh3.googleusercontent.com/-897VXrJB6RE/URquxxxd-5I/AAAAAAAAAbs/j-Cz4T4YvIw/s1024/Leica%25252050mm%252520Summilux.jpg",
			"https://lh5.googleusercontent.com/-qSJ4D4iXzGo/URquyDWiJ1I/AAAAAAAAAbs/k2pBXeWehOA/s1024/Leica%25252050mm%252520Summilux.jpg",
			"https://lh6.googleusercontent.com/-dwlPg83vzLg/URquylTVuFI/AAAAAAAAAbs/G6SyQ8b4YsI/s1024/Leica%252520M8%252520%252528Front%252529.jpg",
			"https://lh3.googleusercontent.com/-R3_EYAyJvfk/URquzQBv8eI/AAAAAAAAAbs/b9xhpUM3pEI/s1024/Light%252520to%252520Sand.jpg",
			"https://lh3.googleusercontent.com/-fHY5h67QPi0/URqu0Cp4J1I/AAAAAAAAAbs/0lG6m94Z6vM/s1024/Little%252520Bit%252520of%252520Paradise.jpg",
			"https://lh5.googleusercontent.com/-TzF_LwrCnRM/URqu0RddPOI/AAAAAAAAAbs/gaj2dLiuX0s/s1024/Lone%252520Pine%252520Sunset.jpg",
			"https://lh3.googleusercontent.com/-4HdpJ4_DXU4/URqu046dJ9I/AAAAAAAAAbs/eBOodtk2_uk/s1024/Lonely%252520Rock.jpg",
			"https://lh6.googleusercontent.com/-erbF--z-W4s/URqu1ajSLkI/AAAAAAAAAbs/xjDCDO1INzM/s1024/Longue%252520Vue.jpg",
			"https://lh6.googleusercontent.com/-0CXJRdJaqvc/URqu1opNZNI/AAAAAAAAAbs/PFB2oPUU7Lk/s1024/Look%252520Me%252520in%252520the%252520Eye.jpg",
			"https://lh3.googleusercontent.com/-D_5lNxnDN6g/URqu2Tk7HVI/AAAAAAAAAbs/p0ddca9W__Y/s1024/Lost%252520in%252520a%252520Field.jpg",
			"https://lh6.googleusercontent.com/-flsqwMrIk2Q/URqu24PcmjI/AAAAAAAAAbs/5ocIH85XofM/s1024/Marshall%252520Beach%252520Sunset.jpg",
			"https://lh4.googleusercontent.com/-Y4lgryEVTmU/URqu28kG3gI/AAAAAAAAAbs/OjXpekqtbJ4/s1024/Mono%252520Lake%252520Blue.jpg",
			"https://lh4.googleusercontent.com/-AaHAJPmcGYA/URqu3PIldHI/AAAAAAAAAbs/lcTqk1SIcRs/s1024/Monument%252520Valley%252520Overlook.jpg",
			"https://lh4.googleusercontent.com/-vKxfdQ83dQA/URqu31Yq_BI/AAAAAAAAAbs/OUoGk_2AyfM/s1024/Moving%252520Rock.jpg",
			"https://lh5.googleusercontent.com/-CG62QiPpWXg/URqu4ia4vRI/AAAAAAAAAbs/0YOdqLAlcAc/s1024/Napali%252520Coast.jpg",
			"https://lh6.googleusercontent.com/-wdGrP5PMmJQ/URqu5PZvn7I/AAAAAAAAAbs/m0abEcdPXe4/s1024/One%252520Wheel.jpg",
			"https://lh6.googleusercontent.com/-6WS5DoCGuOA/URqu5qx1UgI/AAAAAAAAAbs/giMw2ixPvrY/s1024/Open%252520Sky.jpg",
			"https://lh6.googleusercontent.com/-u8EHKj8G8GQ/URqu55sM6yI/AAAAAAAAAbs/lIXX_GlTdmI/s1024/Orange%252520Sunset.jpg",
			"https://lh6.googleusercontent.com/-74Z5qj4bTDE/URqu6LSrJrI/AAAAAAAAAbs/XzmVkw90szQ/s1024/Orchid.jpg",
			"https://lh6.googleusercontent.com/-lEQE4h6TePE/URqu6t_lSkI/AAAAAAAAAbs/zvGYKOea_qY/s1024/Over%252520there.jpg",
			"https://lh5.googleusercontent.com/-cauH-53JH2M/URqu66v_USI/AAAAAAAAAbs/EucwwqclfKQ/s1024/Plumes.jpg",
			"https://lh3.googleusercontent.com/-eDLT2jHDoy4/URqu7axzkAI/AAAAAAAAAbs/iVZE-xJ7lZs/s1024/Rainbokeh.jpg",
			"https://lh5.googleusercontent.com/-j1NLqEFIyco/URqu8L1CGcI/AAAAAAAAAbs/aqZkgX66zlI/s1024/Rainbow.jpg",
			"https://lh5.googleusercontent.com/-DRnqmK0t4VU/URqu8XYN9yI/AAAAAAAAAbs/LgvF_592WLU/s1024/Rice%252520Fields.jpg",
			"https://lh3.googleusercontent.com/-hwh1v3EOGcQ/URqu8qOaKwI/AAAAAAAAAbs/IljRJRnbJGw/s1024/Rockaway%252520Fire%252520Sky.jpg",
			"https://lh5.googleusercontent.com/-wjV6FQk7tlk/URqu9jCQ8sI/AAAAAAAAAbs/RyYUpdo-c9o/s1024/Rockaway%252520Flow.jpg",
			"https://lh6.googleusercontent.com/-6cAXNfo7D20/URqu-BdzgPI/AAAAAAAAAbs/OmsYllzJqwo/s1024/Rockaway%252520Sunset%252520Sky.jpg",
			"https://lh3.googleusercontent.com/-sl8fpGPS-RE/URqu_BOkfgI/AAAAAAAAAbs/Dg2Fv-JxOeg/s1024/Russian%252520Ridge%252520Sunset.jpg",
			"https://lh6.googleusercontent.com/-gVtY36mMBIg/URqu_q91lkI/AAAAAAAAAbs/3CiFMBcy5MA/s1024/Rust%252520Knot.jpg",
			"https://lh6.googleusercontent.com/-GHeImuHqJBE/URqu_FKfVLI/AAAAAAAAAbs/axuEJeqam7Q/s1024/Sailing%252520Stones.jpg",
			"https://lh3.googleusercontent.com/-hBbYZjTOwGc/URqu_ycpIrI/AAAAAAAAAbs/nAdJUXnGJYE/s1024/Seahorse.jpg",
			"https://lh3.googleusercontent.com/-Iwi6-i6IexY/URqvAYZHsVI/AAAAAAAAAbs/5ETWl4qXsFE/s1024/Shinjuku%252520Street.jpg",
			"https://lh6.googleusercontent.com/-amhnySTM_MY/URqvAlb5KoI/AAAAAAAAAbs/pFCFgzlKsn0/s1024/Sierra%252520Heavens.jpg",
			"https://lh5.googleusercontent.com/-dJgjepFrYSo/URqvBVJZrAI/AAAAAAAAAbs/v-F5QWpYO6s/s1024/Sierra%252520Sunset.jpg",
			"https://lh4.googleusercontent.com/-Z4zGiC5nWdc/URqvBdEwivI/AAAAAAAAAbs/ZRZR1VJ84QA/s1024/Sin%252520Lights.jpg",
			"https://lh4.googleusercontent.com/-_0cYiWW8ccY/URqvBz3iM4I/AAAAAAAAAbs/9N_Wq8MhLTY/s1024/Starry%252520Lake.jpg",
			"https://lh3.googleusercontent.com/-A9LMoRyuQUA/URqvCYx_JoI/AAAAAAAAAbs/s7sde1Bz9cI/s1024/Starry%252520Night.jpg",
			"https://lh3.googleusercontent.com/-KtLJ3k858eY/URqvC_2h_bI/AAAAAAAAAbs/zzEBImwDA_g/s1024/Stream.jpg",
			"https://lh5.googleusercontent.com/-dFB7Lad6RcA/URqvDUftwWI/AAAAAAAAAbs/BrhoUtXTN7o/s1024/Strip%252520Sunset.jpg",
			"https://lh5.googleusercontent.com/-at6apgFiN20/URqvDyffUZI/AAAAAAAAAbs/clABCx171bE/s1024/Sunset%252520Hills.jpg",
			"https://lh4.googleusercontent.com/-7-EHhtQthII/URqvEYTk4vI/AAAAAAAAAbs/QSJZoB3YjVg/s1024/Tenaya%252520Lake%2525202.jpg",
			"https://lh6.googleusercontent.com/-8MrjV_a-Pok/URqvFC5repI/AAAAAAAAAbs/9inKTg9fbCE/s1024/Tenaya%252520Lake.jpg",
			"https://lh5.googleusercontent.com/-B1HW-z4zwao/URqvFWYRwUI/AAAAAAAAAbs/8Peli53Bs8I/s1024/The%252520Cave%252520BW.jpg",
			"https://lh3.googleusercontent.com/-PO4E-xZKAnQ/URqvGRqjYkI/AAAAAAAAAbs/42nyADFsXag/s1024/The%252520Fisherman.jpg",
			"https://lh4.googleusercontent.com/-iLyZlzfdy7s/URqvG0YScdI/AAAAAAAAAbs/1J9eDKmkXtk/s1024/The%252520Night%252520is%252520Coming.jpg",
			"https://lh6.googleusercontent.com/-G-k7YkkUco0/URqvHhah6fI/AAAAAAAAAbs/_taQQG7t0vo/s1024/The%252520Road.jpg",
			"https://lh6.googleusercontent.com/-h-ALJt7kSus/URqvIThqYfI/AAAAAAAAAbs/ejiv35olWS8/s1024/Tokyo%252520Heights.jpg",
			"https://lh5.googleusercontent.com/-Hy9k-TbS7xg/URqvIjQMOxI/AAAAAAAAAbs/RSpmmOATSkg/s1024/Tokyo%252520Highway.jpg",
			"https://lh6.googleusercontent.com/-83oOvMb4OZs/URqvJL0T7lI/AAAAAAAAAbs/c5TECZ6RONM/s1024/Tokyo%252520Smog.jpg",
			"https://lh3.googleusercontent.com/-FB-jfgREEfI/URqvJI3EXAI/AAAAAAAAAbs/XfyweiRF4v8/s1024/Tufa%252520at%252520Night.jpg",
			"https://lh4.googleusercontent.com/-vngKD5Z1U8w/URqvJUCEgPI/AAAAAAAAAbs/ulxCMVcU6EU/s1024/Valley%252520Sunset.jpg",
			"https://lh6.googleusercontent.com/-DOz5I2E2oMQ/URqvKMND1kI/AAAAAAAAAbs/Iqf0IsInleo/s1024/Windmill%252520Sunrise.jpg",
			"https://lh5.googleusercontent.com/-biyiyWcJ9MU/URqvKculiAI/AAAAAAAAAbs/jyPsCplJOpE/s1024/Windmill.jpg",
			"https://lh4.googleusercontent.com/-PDT167_xRdA/URqvK36mLcI/AAAAAAAAAbs/oi2ik9QseMI/s1024/Windmills.jpg",
			"https://lh5.googleusercontent.com/-kI_QdYx7VlU/URqvLXCB6gI/AAAAAAAAAbs/N31vlZ6u89o/s1024/Yet%252520Another%252520Rockaway%252520Sunset.jpg",
			"https://lh4.googleusercontent.com/-e9NHZ5k5MSs/URqvMIBZjtI/AAAAAAAAAbs/1fV810rDNfQ/s1024/Yosemite%252520Tree.jpg",
			// Light images
			"http://tabletpcssource.com/wp-content/uploads/2011/05/android-logo.png",
			"http://simpozia.com/pages/images/stories/windows-icon.png",
			"http://radiotray.sourceforge.net/radio.png",
			"http://www.bandwidthblog.com/wp-content/uploads/2011/11/twitter-logo.png",
			"http://weloveicons.s3.amazonaws.com/icons/100907_itunes1.png",
			"http://weloveicons.s3.amazonaws.com/icons/100929_applications.png",
			"http://www.idyllicmusic.com/index_files/get_apple-iphone.png",
			"http://www.frenchrevolutionfood.com/wp-content/uploads/2009/04/Twitter-Bird.png",
			"http://3.bp.blogspot.com/-ka5MiRGJ_S4/TdD9OoF6bmI/AAAAAAAAE8k/7ydKtptUtSg/s1600/Google_Sky%2BMaps_Android.png",
			"http://www.desiredsoft.com/images/icon_webhosting.png",
			"http://goodereader.com/apps/wp-content/uploads/downloads/thumbnails/2012/01/hi-256-0-99dda8c730196ab93c67f0659d5b8489abdeb977.png",
			"http://1.bp.blogspot.com/-mlaJ4p_3rBU/TdD9OWxN8II/AAAAAAAAE8U/xyynWwr3_4Q/s1600/antivitus_free.png",
			"http://cdn3.iconfinder.com/data/icons/transformers/computer.png",
			"http://cdn.geekwire.com/wp-content/uploads/2011/04/firefox.png?7794fe",
			"https://ssl.gstatic.com/android/market/com.rovio.angrybirdsseasons/hi-256-9-347dae230614238a639d21508ae492302340b2ba",
			"http://androidblaze.com/wp-content/uploads/2011/12/tablet-pc-256x256.jpg",
			"http://www.theblaze.com/wp-content/uploads/2011/08/Apple.png",
			"http://1.bp.blogspot.com/-y-HQwQ4Kuu0/TdD9_iKIY7I/AAAAAAAAE88/3G4xiclDZD0/s1600/Twitter_Android.png",
			"http://3.bp.blogspot.com/-nAf4IMJGpc8/TdD9OGNUHHI/AAAAAAAAE8E/VM9yU_lIgZ4/s1600/Adobe%2BReader_Android.png",
			"http://cdn.geekwire.com/wp-content/uploads/2011/05/oovoo-android.png?7794fe",
			"http://icons.iconarchive.com/icons/kocco/ndroid/128/android-market-2-icon.png",
			"http://thecustomizewindows.com/wp-content/uploads/2011/11/Nicest-Android-Live-Wallpapers.png",
			"http://c.wrzuta.pl/wm16596/a32f1a47002ab3a949afeb4f",
			"http://macprovid.vo.llnwd.net/o43/hub/media/1090/6882/01_headline_Muse.jpg",
			// Special cases
			"http://cdn.urbanislandz.com/wp-content/uploads/2011/10/MMSposter-large.jpg", // Very large image
			"http://www.ioncannon.net/wp-content/uploads/2011/06/test9.webp", // WebP image
			"http://4.bp.blogspot.com/-LEvwF87bbyU/Uicaskm-g6I/AAAAAAAAZ2c/V-WZZAvFg5I/s800/Pesto+Guacamole+500w+0268.jpg", // Image with "Mark has been invalidated" problem
			"file:///sdcard/Universal Image Loader @#&=+-_.,!()~'%20.png", // Image from SD card with encoded symbols
			"assets://Living Things @#&=+-_.,!()~'%20.jpg", // Image from assets
			"drawable://" + R.drawable.ic_launcher, // Image from drawables
			"http://upload.wikimedia.org/wikipedia/ru/b/b6/Как_кот_с_мышами_воевал.png", // Link with UTF-8
			"https://www.eff.org/sites/default/files/chrome150_0.jpg", // Image from HTTPS
			"http://bit.ly/soBiXr", // Redirect link
			"http://img001.us.expono.com/100001/100001-1bc30-2d736f_m.jpg", // EXIF
			"", // Empty link
			"http://wrong.site.com/corruptedLink", // Wrong link
	};

	private Constants() {
	}

	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}
	
	public static class Extra {
		public static final String FRAGMENT_INDEX = "com.nostra13.example.universalimageloader.FRAGMENT_INDEX";
		public static final String IMAGE_POSITION = "com.nostra13.example.universalimageloader.IMAGE_POSITION";
	}
}
