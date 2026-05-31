<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Oséa Math

Joc Android de aritmetică pentru Oséa, construit pentru sesiuni scurte de mastery, numărare jucăușă și progres vizibil pentru părinte.

View your app in AI Studio: https://ai.studio/apps/4379ff2f-35fc-4002-9a99-3cd580db04a5

## Direcție premium

Versiunea curentă transformă quiz-ul inițial într-o buclă ghidată de mastery, inspirată de white paper-ul 2 Hour Learning:

- sesiune concentrată de 25 de minute
- ecran de pauză la finalul time-box-ului, ca sesiunea să nu curgă la nesfârșit
- Daily Rings vizibile imediat sus: timp, ținta de azi și siguranța răspunsurilor
- progres vizual prin `Harta Mastery`
- misiune activă pe hartă: aplicația arată următorul port, câte comori mai lipsesc și progresul pe segmentul curent
- `Speed bump` adaptiv după răspunsuri corecte consecutive
- `Struggle support` când materialul devine prea greu
- scădere concretă introdusă adaptiv după primele niveluri de adunare
- încurajări audio offline
- narațiune vocală on-device din butonul `Ascultă`, pentru copil pre-cititor
- sumar pentru părinte: minute, acuratețe, focus și nivel
- progres local persistent: comori totale, sesiuni completate și record personal
- streak zilnic și ultimul raport de sesiune salvate local pentru Parent Dash
- `Jurnal de căpitan` în Parent Dash cu ultimele sesiuni, trend de acuratețe și nivelul atins
- setări părinte persistente, pliate în Parent Dash, pentru țintă zilnică, minute de sesiune și challenge maxim
- colecție de recompense deblocată treptat, ca Oséa să revină la propria aventură
- recompensele folosesc aceleași PNG-uri pirate generate: monedă, hartă, lunetă, busolă, ancoră și cufăr
- obiecte numărabile generate ca PNG-uri premium: corăbii, cufere, bănuți, tunuri, săbii, hărți, lunete, busole, ancore și lopeți de comoară
- set nou de obiecte premium mai mari și mai detaliate: cârmă de corabie, săculeț cu nestemate, ghiulele și felinar
- launcher icon nou, aliniat cu tema de aventură pe mare: corabie, cufăr și monedă
- micro-coaching vizual după răspuns greșit: problema se repară în pași concreți, cu aceleași obiecte din joc
- după greșeală, răspunsurile se blochează din nou până când copilul reface numărarea completă
- după numărare completă, jocul arată vizual `Total sigur` sau `Rămân pe punte`, ca sensul operației să fie concret
- la scădere, rezultatul separă vizual comorile care rămân pe punte de cele mutate în cufăr
- la scădere, copilul mută efectiv primele comori în cufăr cu marcaj `-1`, apoi numără numai ce rămâne pe punte
- feedback haptic discret la atingerea obiectelor și la alegerea răspunsului, ca bucla să se simtă mai tactilă pe Android
- ghid vizual pentru următorul obiect de atins, cu contur luminos și numărul pasului următor
- numărarea avansează doar când este atins obiectul luminat, ca să reducă tapping-ul la întâmplare
- micro-celebrare scurtă după răspuns corect: `Comoară +1`, streak și colecție, fără să rupă ritmul sesiunii
- ecran final de sesiune cu raport scurt: minute, acuratețe, reparări și comori totale
- `Parent Dash` cu semnal de potrivire a dificultății, după logica din PDF: peste 95% poate fi prea ușor, sub 70% intră suportul
- `Guess Guard` în Parent Dash: scor de eficiență inspirat de controlul de calitate/waste din PDF, bazat pe acuratețe, reparații și greșeli consecutive
- `Următorul pas` în Parent Dash: recomandare scurtă pentru sesiunea următoare, bazată pe eficiență și skill gaps
- `Learning Plan` cu metrici separate pentru Adunare și Scădere, ca skill gaps să fie vizibile rapid

Vezi `docs/premium-learning-plan.md` pentru principiile de produs și următoarele milestone-uri.

## Run Locally

**Prerequisites:**  [Android Studio](https://developer.android.com/studio)

The current Android Gradle Plugin requires Gradle `9.3.1+` and a valid Android SDK path via `ANDROID_HOME` or `local.properties`.


1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project.
4. Create a file named `.env` in the project directory and set `GEMINI_API_KEY` in that file to your Gemini API key (see `.env.example` for an example)
5. Remove this line from the app's `build.gradle.kts` file: `signingConfig = signingConfigs.getByName("debugConfig")`
6. Run the app on an emulator or physical device

## Verified locally

This branch was verified with:

- local Gradle `9.3.1` under `.gradle-local`
- local Android SDK under `.android-sdk`
- JDK 21 with `jlink`
- `testDebugUnitTest`
- `assembleDebug`
- `recordRoborazziDebug`

Generated debug APK: `app/build/outputs/apk/debug/app-debug.apk`.
Latest visual QA screenshot: `app/src/test/screenshots/greeting.png`.
