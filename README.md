<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Oséa Math

Joc Android de aritmetică pentru Oséa, construit pentru sesiuni scurte de mastery, numărare jucăușă și progres vizibil pentru părinte.

View your app in AI Studio: https://ai.studio/apps/4379ff2f-35fc-4002-9a99-3cd580db04a5

## Direcție premium

Versiunea curentă transformă quiz-ul inițial într-o buclă ghidată de mastery, inspirată de white paper-ul 2 Hour Learning:

- sesiune concentrată de 25 de minute
- onboarding scurt la prima rulare, cu preset recomandat pentru 4 ani: 8 comori, 10 minute și nivel 3
- ecran de pauză la finalul time-box-ului, ca sesiunea să nu curgă la nesfârșit
- Daily Rings vizibile imediat sus: timp, ținta de azi și siguranța răspunsurilor
- `Misiuni de azi`: un board vizual cu ținta zilnică, siguranța răspunsurilor și următoarea comoară de colecție
- `Focus Mastery` pe fiecare rundă, ca Oséa și părintele să vadă ce abilitate se antrenează chiar acum
- progres vizual prin `Harta Mastery`
- misiune activă pe hartă: aplicația arată următorul port, câte comori mai lipsesc și progresul pe segmentul curent
- hartă de voiaj cu traseu desenat, corabie animată și surprize vizuale pe drum spre comoară
- insulele de pe hartă au forme desenate și marker-e cu obiecte reale: ancoră, monedă, cufăr și steag de comoară
- `Speed bump` adaptiv după răspunsuri corecte consecutive
- `Struggle support` când materialul devine prea greu
- `Port sigur` după struggle detector: următoarea rundă revine intenționat la comori mici până la 4, pentru consolidarea bazei
- motor adaptiv pentru următoarea problemă: adunarea trebuie să fie solidă înainte de minus, iar scăderea revine când apare skill gap
- scădere concretă introdusă adaptiv după primele niveluri de adunare
- încurajări audio offline
- narațiune vocală on-device din butonul `Ascultă`, pentru copil pre-cititor
- sumar pentru părinte: minute, acuratețe, focus și nivel
- progres local persistent: comori totale, sesiuni completate și record personal
- streak zilnic și ultimul raport de sesiune salvate local pentru Parent Dash
- `Jurnal de căpitan` în Parent Dash cu ultimele sesiuni, trend de acuratețe și nivelul atins
- sumar săptămânal în Parent Dash: sesiuni, minute, acuratețe medie și recomandare scurtă
- `Audit părinte` în Parent Dash: timp de lucru, calitate/eficiență și skill gap, inspirat de raportarea din PDF
- setări părinte persistente, pliate în Parent Dash, pentru țintă zilnică, minute de sesiune și challenge maxim
- colecție de recompense deblocată treptat, ca Oséa să revină la propria aventură
- progres vizual către următoarea recompensă, cu praguri de comori și rarități `Comun`/`Rar`/`Legendar`
- recompensele folosesc aceleași PNG-uri pirate generate: monedă, hartă, lunetă, busolă, ancoră și cufăr
- obiecte numărabile generate ca PNG-uri premium: corăbii, cufere, bănuți, tunuri, săbii, hărți, lunete, busole, ancore și lopeți de comoară
- set extins de obiecte premium mai mari și mai detaliate: cârmă de corabie, săculeț cu nestemate, ghiulele, felinar, pergament, chei, rubine, pumnale, inele, sticle cu mesaj, scoici cu perle, sextante și steaguri de comoară
- surprize de hartă extinse cu noile PNG-uri generate: sticlă cu mesaj, scoică regală, sextant de aur și steag de comoară
- cufăr vizual care se umple cu monede și nestemate pe măsură ce se strâng comori
- launcher icon nou, aliniat cu tema de aventură pe mare: corabie, cufăr și monedă
- micro-coaching vizual după răspuns greșit: problema se repară în pași concreți, cu aceleași obiecte din joc
- traseu vizual de reparare după răspuns greșit: încercarea pleacă la doc, apoi copilul ajunge la cufărul cu răspuns sigur
- după greșeală, răspunsurile se blochează din nou până când copilul reface numărarea completă
- după numărare completă, jocul arată vizual `Total sigur` sau `Rămân pe punte`, ca sensul operației să fie concret
- în timpul numărării, o bandă de aventură mută lopata spre cufăr și arată câte atingeri mai lipsesc
- răspunsurile numerice arată ca mici cufere de joc, cu monede, sparkle și feedback vizual pentru corect/greșit
- la scădere, rezultatul separă vizual comorile care rămân pe punte de cele mutate în cufăr
- la scădere, copilul mută efectiv primele comori în cufăr cu marcaj `-1`, apoi numără numai ce rămâne pe punte
- feedback haptic discret la atingerea obiectelor și la alegerea răspunsului, ca bucla să se simtă mai tactilă pe Android
- ghid vizual pentru următorul obiect de atins, cu contur luminos și numărul pasului următor
- panou vizual de pași pentru scădere: `-1 Mută în cufăr`, apoi `= Numără ce rămâne`, apoi alegerea răspunsului
- numărarea avansează doar când este atins obiectul luminat, ca să reducă tapping-ul la întâmplare
- micro-celebrare scurtă după răspuns corect: `Comoară +1`, sparkle, monede care zboară spre cufăr, surpriză de pe hartă, streak și progres spre următoarea recompensă, fără să rupă ritmul sesiunii
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
Recovery mission visual QA: `app/src/test/screenshots/recovery_mission_stage.png`.
Counting trail visual QA: `app/src/test/screenshots/new_treasure_items.png` and `app/src/test/screenshots/subtraction_stage.png`.
Premium answer chest QA: `app/src/test/screenshots/answer_chests.png` and `app/src/test/screenshots/locked_answer_chests.png`.
Reward harbor visual QA: `app/src/test/screenshots/reward_harbor_progress.png`.
Onboarding visual QA: `app/src/test/screenshots/onboarding.png`.
Daily quests visual QA: `app/src/test/screenshots/captain_quest_board.png`.
Reward burst visual QA: `app/src/test/screenshots/reward_burst.png`.
Correct streak cannon salute QA is included in `app/src/test/screenshots/reward_burst.png`.
Locked answer chests visual QA: `app/src/test/screenshots/locked_answer_chests.png`.
Answer unlock banner visual QA: `app/src/test/screenshots/answer_unlock_banner.png`.
Repair harbor visual QA: `app/src/test/screenshots/mastery_repair_card.png`.
Treasure celebration visual QA: `app/src/test/screenshots/celebration_treasure.png`.
Generated item pack: captain hat, ship bell, rope coil, jeweled crown, message bottle, pearl shell, sextant, pirate flag, captain boot, powder keg, treasure pickaxe, emerald relic.
Relic trail visual QA is included in `app/src/test/screenshots/greeting.png`; generated item QA is in `app/src/test/screenshots/new_treasure_items.png`.
Offline sound pack: counting tap, move-to-chest, count complete, answer select, treasure unlock, streak cannon salute, correct/wrong reward, and victory cues.
