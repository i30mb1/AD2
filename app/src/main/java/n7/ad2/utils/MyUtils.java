package n7.ad2.utils;

public class MyUtils {
//
//    public static String getTextFromAsset(Context context, String fileName) {
//        AssetManager assetManager = context.getAssets();
//        String text;
//        InputStream inputStream;
//        try {
//            inputStream = assetManager.open(fileName);
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            int i;
//            i = inputStream.read();
//            while (i != -1) {
//                byteArrayOutputStream.write(i);
//                i = inputStream.read();
//            }
//            inputStream.close();
//            text = byteArrayOutputStream.toString();
//        } catch (IOException e) {
//            e.printStackTrace();
//            text = "";
//        }
//        return text;
//    }
//
//    public static Drawable loadImageHeroFromAsset(Context context, String hero, String file) {
//        InputStream ims=null;
//        try {
//            ims = context.getAssets().open(String.format("heroes/%s/%s", hero, file));
//            if (ims != null) {
//                return Drawable.createFromStream(ims, null);
//            }
//            return null;
//        } catch (IOException ex) {
//            try {
//                ims = context.getAssets().open(String.format("heroes/%s/%s", hero, file.replace(".jpg",".webp")));
//                if (ims != null) {
//                    return Drawable.createFromStream(ims, null);
//                }
//            } catch (IOException e) {
//                try {
//                    ims = context.getAssets().open(String.format("heroes/%s/%s", hero, file.replace(".png",".webp")));
//                    if (ims != null) {
//                        return Drawable.createFromStream(ims, null);
//                    }
//                } catch (IOException e4) {
//                    e.printStackTrace();
//                }
//            }
//            return Drawable.createFromStream(ims, null);
//        }
//    }
//
//    public static Drawable loadImageItemFromAsset(Context context, String file) {
//        try {
//            if (file.startsWith("tango_")) file = "tango_single.webp";
//            if(file.startsWith("dagon")) file = "dagon.webp";
//            InputStream ims = context.getAssets().open(String.format("items/%s",file));
//            if (ims != null) {
//                return Drawable.createFromStream(ims, null);
//            }
//            return null;
//        } catch (IOException ex) {
//            try {
//                InputStream ims = context.getAssets().open(String.format("items/%s",file.replace("png","webp")));
//                if (ims != null) {
//                    return Drawable.createFromStream(ims, null);
//                }
//                return null;
//            } catch (IOException ex2) {
//                return null;
//            }
//        }
//    }
//
//    public static boolean isNetworkAvailable(Context context) {
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
//        return networkInfo != null && networkInfo.isConnected();
//    }
//
//    public static int dpSize(Context context, int px) {
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (px * scale + 0.5f);
//    }
//
//    public static Bitmap getBitmapFromAsset(Context context, String strName) {
//        Bitmap bitmap;
//        try {
//            strName= strName.replace("outworld_devourer","obsidian_destroyer");
//            strName= strName.replace("queen_of_pain","queenofpain");
//            strName= strName.replace("/underlord","/abyssal_underlord");
//            strName= strName.replace("clockwerk","rattletrap");
//            strName= strName.replace("/io","/wisp");
//            strName= strName.replace("lifestealer","life_stealer");
//            strName= strName.replace("nature's_prophet","furion");
//            strName= strName.replace("nature's_prophet","ancient_apparation");
//            strName= strName.replace("zeus","zuus");
//            strName= strName.replace("timbersaw","shredder");
//            strName= strName.replace("magnus","magnataur");
//            strName= strName.replace("wraith_king","skeleton_king");
//            strName= strName.replace("vengeful_spirit","vengefulspirit");
//            strName= strName.replace("anti-mage","antimage");
//            strName= strName.replace("centaur_warrunner","centaur");
//            strName= strName.replace("treant_protector","treant");
//            strName= strName.replace("necrophos","necrolyte");
//            strName= strName.replace("shadow_fiend","nevermore");
//            strName= strName.replace("windranger","windrunner");
//            strName= strName.replace("heroes/nevermore/4","heroes/nevermore/6");
//            strName= strName.replace("heroes/nevermore/3","heroes/nevermore/5");
//            strName= strName.replace("heroes/nevermore/2","heroes/nevermore/4");
//            strName= strName.replace("heroes/morphling/4","heroes/morphling/6");
//            strName= strName.replace("heroes/morphling/3","heroes/morphling/4");
//            strName= strName.replace("heroes/morphling/3","heroes/morphling/4");
//            strName= strName.replace("heroes/monkey_king/4","heroes/monkey_king/8");
//            strName= strName.replace("heroes/phoenix/4","heroes/phoenix/8");
//            strName= strName.replace("heroes/phoenix/3","heroes/phoenix/5");
//            strName= strName.replace("heroes/phoenix/2","heroes/phoenix/3");
//            strName= strName.replace("heroes/wisp/4","heroes/wisp/7");
//            strName= strName.replace("heroes/wisp/3","heroes/wisp/6");
//            strName= strName.replace("heroes/wisp/2","heroes/wisp/3");
//            bitmap = BitmapFactory.decodeStream(context.getAssets().open(strName));
//        } catch (IOException e) {
//            strName= strName.replace(".png",".webp");
//            try {
//                bitmap = BitmapFactory.decodeStream(context.getAssets().open(strName));
//            } catch (IOException e1) {
//                bitmap = null;
//            }
//        }
//        return bitmap;
//    }
//
//    public static void startSpriteAnim(Context context, final Toolbar img, String nameSprite, Boolean isLooped, int mActionBarSize) {
//        Bitmap birdBmp = getBitmapFromAssets(context,nameSprite);
//        if (birdBmp != null) {
//            // cut bitmaps from bird to array of bitmaps
//            Bitmap[] bmps;
//            int FRAME_W = 32;
//            int FRAME_H = 32;
//            int NB_FRAMES = (birdBmp.getWidth()/32);
//            int FRAME_DURATION = 70; // in ms !
//            int SCALE_FACTOR = 1;
//            int COUNT_X = (birdBmp.getWidth()/32);
//            int COUNT_Y = 1;
//
//            bmps = new Bitmap[NB_FRAMES];
//            int currentFrame = 0;
//
//            for (int i = 0; i < COUNT_Y; i++) {
//                for (int j = 0; j < COUNT_X; j++) {
//                    bmps[currentFrame] = Bitmap.createBitmap(birdBmp, FRAME_W * j, FRAME_H * i, FRAME_W, FRAME_H);
//                    // apply scale factor
//                    bmps[currentFrame] = Bitmap.createScaledBitmap(bmps[currentFrame], mActionBarSize, mActionBarSize, true);
//                    if (++currentFrame >= NB_FRAMES) {
//                        break;
//                    }
//                }
//            }
//            // create animation programmatically
//            final AnimationDrawable animation = new AnimationDrawable();
//            animation.setOneShot(isLooped); // repeat animation
//
//            for (int i = 0; i < NB_FRAMES; i++) {
//                animation.addFrame(new BitmapDrawable(context.getResources(), bmps[i]), FRAME_DURATION);
//            }
//            // load animation on image
//            img.setNavigationIcon(animation);
//            // start animation on image
//            img.post(new Runnable() {
//                @Override
//                public void run() {
//                    animation.start();
//                }
//            });
//        }
//    }
//
//    public static void startSpriteAnim(Context context, final ImageView img, String nameSprite, Boolean isLooped, int width, int hight,int duration,int scale) {
//        try {
//            Bitmap birdBmp = getBitmapFromAssets(context, nameSprite);
//            if (birdBmp != null) {
//                // cut bitmaps from bird to array of bitmaps
//                Bitmap[] bmps;
//                int NB_FRAMES = (birdBmp.getWidth() / width);
//                int COUNT_X = (birdBmp.getWidth() / width);
//                int COUNT_Y = 1;
//
//                bmps = new Bitmap[NB_FRAMES];
//                int currentFrame = 0;
//
//                for (int i = 0; i < COUNT_Y; i++) {
//                    for (int j = 0; j < COUNT_X; j++) {
//                        bmps[currentFrame] = Bitmap.createBitmap(birdBmp, width * j, hight * i, width, hight);
//                        // apply scale factor
//                        bmps[currentFrame] = Bitmap.createScaledBitmap(bmps[currentFrame], width * scale, hight * scale, true);
//                        if (++currentFrame >= NB_FRAMES) {
//                            break;
//                        }
//                    }
//                }
//                // create animation programmatically
//                final AnimationDrawable animation = new AnimationDrawable();
//                animation.setOneShot(!isLooped); // repeat animation
//
//                for (int i = 0; i < NB_FRAMES; i++) {
//                    animation.addFrame(new BitmapDrawable(context.getResources(), bmps[i]), duration);
//                }
//                // load animation on image
//                img.setImageDrawable(animation);
//                // start animation on image
//                img.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        animation.start();
//                    }
//                });
//            }
//        } catch (OutOfMemoryError | NullPointerException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static Bitmap getBitmapFromAssets(Context context, String filepath) {
//        AssetManager assetManager = context.getAssets();
//        InputStream istr = null;
//        Bitmap bitmap = null;
//
//        try {
//            istr = assetManager.open(filepath);
//            bitmap = BitmapFactory.decodeStream(istr);
//        } catch (IOException ioe) {
//            try {
//                istr = assetManager.open(filepath.replace(".png", "webp"));
//                bitmap = BitmapFactory.decodeStream(istr);
//            } catch (IOException ioe3) {
//                // manage exception
//
//            } finally {
//                if (istr != null) {
//                    try {
//                        istr.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            return bitmap;
//        }
//        return bitmap;
//    }

}
