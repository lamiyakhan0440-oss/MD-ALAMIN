# MoviesPlus Native Android Starter

এটি Kotlin + Jetpack Compose-ভিত্তিক Native Android starter app। WebView ব্যবহার করে না।
এটি আপনার PHP REST API-এর query-string route format ব্যবহার করে।

## আগে যা করতে হবে
1. `MoviesPlus-PHP-REST-API.zip` থেকে `api/` ফোল্ডারটি website root-এ ইনস্টল করুন।
2. আপনার API endpoint browser-এ পরীক্ষা করুন।
3. `app/src/main/java/com/moviesplus/app/ApiClient.kt`-এ `BASE_URL` আপনার সঠিক API URL দিন।
4. Android Studio-তে এই ফোল্ডার খুলে Gradle sync করে APK build করুন।

## বর্তমান starter-এ
- Native Compose UI
- Home / Movies / Music / Reels / Live TV navigation
- PHP API থেকে JSON fetch
- Movies, music, reels, TV list-এর basic cards
- Loading/error state

## এখনো production-complete নয়
Login/register UI ও secure token persistence, series episode screen, ExoPlayer playback, downloads, watch party realtime, subscriptions/payment verification, push notifications, ad SDK এবং admin-controlled app branding পরের ধাপে যুক্ত করতে হবে।
আপনার website database/schema না যাচাই করে payment activation বা premium access client-side থেকে চালু করা হয় না।

## API format
`https://YOUR-DOMAIN/api/?path=home`
`https://YOUR-DOMAIN/api/?path=movies`
`https://YOUR-DOMAIN/api/?path=music`
`https://YOUR-DOMAIN/api/?path=reels`
`https://YOUR-DOMAIN/api/?path=tv`

নোট: এই project skeleton-এর APK build বাস্তব Android build environment-এ পরীক্ষা করা বাকি।
