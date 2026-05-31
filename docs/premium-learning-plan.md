# Oséa Math: Plan Premium De Învățare

## Principii Aplicate Din White Paper-ul 2 Hour Learning

- Mastery înainte de progres: Oséa avansează doar după răspunsuri corecte repetate, iar sesiunea zilnică are o țintă clară.
- Scădere după bazele de adunare: operațiile cu minus apar adaptiv doar de la nivelul 3, ca extensie a mastery-ului, nu ca încărcare timpurie.
- Sesiuni scurte și concentrate: aplicația folosește un inel de sesiune de 25 de minute, aliniat cu structura din white paper.
- Pauză după time-box: când timpul sesiunii se termină, jocul oprește răspunsurile și afișează un ecran calm de pauză cu sumar.
- Daily Rings la începutul sesiunii: copilul vede imediat timpul, ținta zilei și siguranța răspunsurilor înainte de exercițiu.
- Progres vizual: noua `Harta Mastery` arată pași mici de învățare în locul unui scor generic, cu insula activă și comorile rămase până la următorul port.
- Daily completion check: ecranul de final rezumă minutele, acuratețea, reparările și comorile, astfel încât copilul și părintele văd rapid ce s-a întâmplat.
- Goal setting pentru părinte: ținta zilnică, durata sesiunii și challenge-ul maxim pot fi ajustate dintr-o secțiune pliabilă în Parent Dash și rămân salvate local.
- Challenge adaptiv: trei răspunsuri corecte activează `Speed bump`; două greșeli activează `Struggle support` și scad dificultatea.
- Selector adaptiv de operații: minusul apare doar după semnale bune la adunare, respectă challenge-ul maxim al părintelui și revine intenționat când scăderea are acuratețe scăzută.
- Feedback auditiv și vizual imediat: audio-ul offline rămâne, împreună cu animații, mesaje de coaching și narațiune TTS on-device prin butonul `Ascultă`.
- Recompensă imediată, scurtă: după un răspuns corect apare `Comoară +1`, streak-ul și colecția, apoi jocul continuă automat.
- Vizibilitate pentru părinte: `Parent Dash` rezumă minutele, acuratețea, focusul și nivelul curent fără să întrerupă copilul.
- Jurnal pentru părinte: ultimele sesiuni rămân salvate local, cu trend simplu de acuratețe și nivel, ca progresul să nu fie doar „ultima sesiune”.
- Learning Plan pentru părinte: aplicația marchează dacă dificultatea pare potrivită, prea ușoară sau prea grea, folosind pragurile 95%/70% descrise în PDF.
- Skill gaps vizibile: Parent Dash separă acuratețea pentru `Adunare` și `Scădere`, cu stare de calibrare până există suficiente încercări.
- Guess Guard pentru părinte: Parent Dash include un scor de eficiență inspirat de semnalul de quality control/waste din PDF, dar formulat blând pentru vârsta lui Oséa.
- Sprijin pentru vârstă mică: fiecare problemă este concretă, atinsă cu degetul și numărabilă înainte de alegerea răspunsului numeric.
- Rezultat concret după numărare: aplicația afișează `Total sigur` sau `Rămân pe punte` doar după ce toate obiectele au fost atinse.
- Scădere concretă: copilul pornește cu obiectele de pe punte, mută primele comori în cufăr cu marcaj `-1`, apoi numără numai ce rămâne pe punte.
- Reducerea ghicitului: răspunsurile se activează doar după ce toate obiectele au fost atinse și numărate; după greșeală, numărarea se resetează și copilul repară concret înainte de o nouă alegere.
- Feedback tactil: fiecare atingere de obiect și fiecare alegere de răspuns declanșează haptic discret, ca acțiunile să se simtă fizice pe device.
- Ghid vizual de numărare: următorul obiect neatins este luminat și primește numărul pasului următor; doar acel obiect poate avansa numărătoarea.
- Continuitate între sesiuni: aplicația salvează local comorile totale, sesiunile finalizate și recordul de streak.
- Continuitate între zile: streak-ul zilnic, ultimul raport și `Jurnalul de căpitan` rămân salvate pentru Parent Dash.
- Recompense vizibile: `Colecția lui Oséa` deblochează obiecte ilustrate după acumularea de comori, nu simboluri abstracte.

## Direcția Curentă A Produsului

Prima versiune upgradată păstrează fantezia marină, dar mută experiența de la quiz simplu la o buclă ghidată de mastery:

1. Oséa vede o misiune și atinge fiecare obiect pentru a număra.
2. Daily Rings îi arată din primul ecran timpul, ținta de azi și siguranța.
3. Dacă are nevoie, apasă `Ascultă` și primește narațiune vocală pentru problemă, numărare sau reparare.
4. Aplicația oferă un set mic de răspunsuri.
5. Răspunsurile corecte umplu ținta zilnică și duc harta înainte, segment cu segment, spre următorul port.
6. Răspunsurile corecte primesc o micro-celebrare rapidă, apoi jocul continuă fără pauze lungi.
7. Răspunsurile greșite activează coaching calm, nu pedeapsă.
8. Micro-coach-ul afișează `Plan de reparare`: primul grup, al doilea grup și totalul corect, cu aceleași obiecte vizuale.
9. După primele niveluri, apar scăderi concrete: copilul mută comori de pe punte în cufăr și apoi numără ce a rămas.
10. După numărare completă, copilul vede o confirmare concretă a rezultatului înainte să aleagă butonul numeric.
11. Pentru scădere, confirmarea arată separat câte comori rămân pe punte și câte au plecat în cufăr.
12. După o greșeală, aplicația blochează răspunsurile și cere recount complet, pentru a evita apăsările la întâmplare.
13. Următoarea problemă se adaptează la streak, la setarea maximă a părintelui și la skill gap-ul dintre adunare/scădere.
14. Dacă time-box-ul se termină înainte de ținta zilnică, apare `Pauză de punte`, iar răspunsurile nu mai pot fi apăsate.
15. La final apare raportul de sesiune cu datele esențiale și o recomandare pentru următorul nivel.
16. Parent Dash păstrează ultima sesiune, streak-ul zilnic și ultimele intrări din `Jurnalul de căpitan`, ca progresul să fie vizibil și după redeschiderea aplicației.
17. Learning Plan arată separat dacă adunarea sau scăderea are nevoie de lucru.
18. Parent Dash arată `Guess Guard`, ca părintele să vadă rapid dacă eficiența scade prin reparații sau greșeli consecutive.
19. Parent Dash oferă `Următorul pas`, o recomandare scurtă pentru sesiunea următoare.
20. Părintele poate ajusta sesiunea: 8/12/16 comori, 10/15/25 minute și challenge ușor/minus/full.
21. Progresul rămâne salvat local, astfel încât următoarea sesiune pornește cu aceeași colecție și același istoric.

## Următoarele Milestone-uri Premium

- Profil local persistent cu streak-uri zilnice, recompense deblocate și istoric pentru părinte.
- Extinderea asset pack-ului cu animații ușoare și variante rare pentru recompensele vizuale de colecție.
- Extinderea narațiunii vocale cu voci înregistrate custom pentru Osea, dacă TTS-ul device-ului nu e suficient de cald.
- Ecran detaliat doar pentru părinte: istoric pe zile, skill gaps, timp de lucru și pași recomandați.
- Onboarding scurt pentru alegerea duratei sesiunii și a sumelor maxime potrivite vârstei.

## Asset-uri Vizuale

- `img_osea_cove_background.png`: fundal ilustrat nou, generat cu skill-ul `imagegen`, salvat local în `app/src/main/res/drawable`.
- Prompt folosit: scenă marină premium pentru joc educațional Android, fără text sau UI, cu zonă centrală calmă pentru componentele aplicației.
- Obiectele numărabile folosesc acum un asset pack generat cu `imagegen`: corabie, cufăr, bănuț, tun decorativ, sabie de căpitan, hartă, lunetă, busolă, ancoră, lopată, cârmă de corabie, săculeț cu nestemate, ghiulele și felinar.
- Fiecare obiect a fost generat pe chroma-key și convertit local în PNG transparent, salvat ca `item_*.png` în `app/src/main/res/drawable`.
- Recompensele din `Colecția lui Oséa` refolosesc aceleași asset-uri premium pentru monedă, hartă, lunetă, busolă, ancoră și cufăr, cu stare vizuală blocată/deblocată.
- Launcher icon-ul folosește o compoziție locală din corabie, cufăr și monedă, cu fundal oceanic, pentru ca APK-ul instalat să nu mai apară cu icon generic.

## Verificare Tehnică

- `testDebugUnitTest` trece cu Gradle 9.3.1, Android SDK local și JDK 21 complet.
- `assembleDebug` trece după reconstruirea locală a `debug.keystore` din `debug.keystore.base64`.
- APK-ul debug generat local se află în `app/build/outputs/apk/debug/app-debug.apk`.
- `recordRoborazziDebug` generează un screenshot valid la `app/src/test/screenshots/greeting.png`.
- QA vizual Pixel 8: bucla principală a fost reordonată ca problema, coach-ul și răspunsurile să fie vizibile înainte de dashboard/recompense.
- QA vizual Pixel 8: obiectele numărabile au fost înlocuite cu ilustrații locale pentru un look mai consistent și mai premium.
- QA vizual Pixel 8: colecția de recompense a fost mutată de la simboluri text la obiecte PNG generate, pentru consistență cu tema de pirați și comori.
- QA vizual Pixel 8: ecranul `Pauză de punte` are captură Roborazzi dedicată pentru time-box.
- QA vizual Pixel 8: `Parent Dash` cu `Jurnal de căpitan` are captură Roborazzi dedicată pentru trend și recomandarea următorului pas.
- QA vizual Pixel 8: micro-coach-ul și `Learning Plan` compilează în aceeași buclă Compose și păstrează răspunsurile blocate până la numărare completă, inclusiv după o reparație.
- QA tehnic: ecranul de final folosește același `GameState` și compilează în build-ul debug, cu layout scrollable pentru telefoane mici.
- QA tehnic: narațiunea folosește Android `TextToSpeech` local, inițializat doar la apăsarea butonului `Ascultă`, fără dependență de internet.
- QA tehnic: streak-ul zilnic are test unitar pentru zile consecutive, aceeași zi și pauze între sesiuni.
- QA tehnic: `Jurnalul de căpitan` are test unitar pentru ordonarea sesiunilor, limitarea istoricului și eticheta de trend.
- QA tehnic: recomandarea `Următorul pas` are test unitar pentru risc de ghicit, introducerea scăderii și gap pe scădere.
- QA tehnic: selectorul adaptiv de operații are test unitar pentru protejarea adunării, introducerea scăderii, respectarea limitei părintelui și repararea gap-ului pe minus.
- QA tehnic: calculul răspunsului are test unitar pentru adunare și scădere, iar scăderea folosește aceeași buclă de numărare/reparare.
- QA tehnic: reprezentarea vizuală a scăderii are test unitar pentru comori rămase și comori mutate în cufăr.
- QA tehnic: interacțiunea de scădere are test unitar pentru mutarea în cufăr și numărarea restului, fără al doilea grup numărat ca adunare.
- QA tehnic: deblocarea răspunsurilor are test unitar separat, ca butoanele să rămână blocate până când toate obiectele vizibile au fost atinse.
- QA tehnic: ghidul de numărare are test unitar separat pentru ordinea stânga-dreapta a obiectelor vizibile.
- QA tehnic: tap-urile în afara obiectului luminat au test unitar și nu modifică numărătoarea.
- QA tehnic: scorul `Guess Guard` are test unitar pentru acuratețe, reparații, greșeli consecutive și etichete de calibrare/risc.
- QA tehnic: progresul Daily Rings are test unitar pentru total zero, valori negative și depășirea țintei.
- QA tehnic: harta de aventură are test unitar pentru insula activă, comorile rămase și progresul pe segment.
- QA tehnic: finalul time-box-ului are test unitar, ca pauza să apară doar când timpul s-a terminat și nu când ținta e deja celebrată.
- QA tehnic: acuratețea per-skill are test unitar pentru semnal gol, progres normal și răspunsuri complet greșite.
- QA vizual: previzualizarea rezultatului se află în `ProblemStage` și apare numai când numărarea este completă.
- QA tehnic: setările de sesiune sunt persistate în `SharedPreferences`, aplicate imediat în `GameState` și ținute pliate implicit ca să nu distragă copilul.
