## NP-fullständighetsreduktioner - Rollbesättning

Målen för labb 4 är att du ska

- öva på att hitta ett lämpligt problem att reducera till ett problem som ska visas NP-svårt,
- konstruera och implementera en enklare polynomisk reduktion mellan givna problem,
- visa att ett problem är NP-fullständigt.

Ansvarig för castingen på ett filmbolag behöver koppla ihop rätt skådespelare med rätt roller. Samma person kan spela flera roller, men samma roll kan endast innehas av en person. Manus anger vilka roller som är med i samma scener. Inga monologer får förekomma. Varje skådespelare får bara ha en roll i varje scen. Varje roll måste förekomma i minst en scen.

Dessutom är divorna p1 och p2 garanterade att få (minst) en roll var (och de rollerna ska självklart delta i åtminstone en scen var). Detta medför extraarbete eftersom de båda inte tål varandra och rollerna ska besättas så att de aldrig spelar mot varandra. Rollbesättningsproblemet är att avgöra ifall alla roller kan besättas med de skådespelare som finns till hands. 

Ingående parametrar är alltså: 

    Roller r1, r2,... , rn 
    Skådespelare p1, p2,... ,pk 
    Villkor typ 1 (till varje roll): rt kan besättas av p1, p2, p6 
    Villkor typ 2 (till varje scen): i su medverkar r1, r3, r5, r6 och r7
    
### Indataformat
     De tre första raderna består av talen n, s och k (antal roller, antal scener och antal skådespelare, n≥1, s≥1, k≥2), ett tal per rad. 
     De följande n raderna representerar villkoren av typ 1 och börjar med ett tal som anger antalet efterföljande tal på raden, 
     följt av de möjliga skådespelarnas nummer (mellan 1 och k, kursiverade i exemplen nedan). 
     De sista s raderna är villkor av typ 2 och börjar ett tal som anger antalet efterföljande tal på raden, 
     följt av tal som representerar de olika rollerna som är med i respektive scen. 
     Varje roll förekommer högst en gång på varje sådan rad, så antalet roller på en rad ligger mellan 2 och n.

Fråga: Kan rollerna besättas med högst k st skådespelare så att p1 och p2 deltar men inte är med i samma scener som varandra?

Exempel på godkända indata:

#### Nej-instans:
```bash
5
5 
3
3 1 2 3
2 2 3
2 1 3
1 2
3 1 2 3
2 1 2
2 1 2
3 1 3 4
2 3 5
3 2 3 5
```

#### Ja-instans:
```bash
6
5
4
3 1 3 4
2 2 3
2 1 3
1 2
4 1 2 3 4
2 1 4
3 1 2 6
3 2 3 5
3 2 4 6
3 2 3 6
2 1 6 
```
### Uppgift
I den här laborationen ska du visa att rollbesättningsproblemet är NP-svårt genom att reducera ett känt NP-fullständigt problem. 
Du får välja mellan att reducera problemen Graffärgning och Hamiltonsk cykel. 

Indataformat för dessa problem beskrivs nedan. 

Din uppgift är alltså att implementera en reduktion, inte att lösa problemet. 
En Karpreduktion transformerar en instans av ett beslutsproblem (A) till en instans av 
ett annat beslutsproblem (B). 

Programmet ska alltså som indata ta en A-instans och som 
utdata generera en B-instans. Tänk noga igenom vilket problem som är A och vilket som är B.

### Graffärgning
Indata: En oriktad graf och ett antal färger m. Isolerade hörn och dubbelkanter kan förekomma, inte öglor.

Fråga: Kan hörnen i grafen färgas med högst m färger så att inga grannar har samma färg?

#### Indataformat: 

    Rad ett: tal V (antal hörn,  1 ≤ V ≤ 300
    Rad två: tal E (antal kanter, 0 ≤ E ≤ 25000) 
    Rad tre: mål m (max antal färger, 1 ≤ m ≤ 2^303) 
    En rad för varje kant (E stycken) med kantens ändpunkter (hörnen numreras från 1 till V)

### Hamiltonsk cykel
Indata: En riktad graf.

Fråga: Finns det en tur längs kanter i grafen som börjar och slutar på samma ställe och som passerar varje hörn exakt en gång?

#### Indataformat: 
    Rad ett: tal V (antal hörn, 1 ≤ V ≤ 200) 
    Rad två: tal E (antal kanter, 0 ≤ E ≤ 5000) 
    En rad för varje kant (E stycken) med kantens starthörn och sluthörn (hörnen numreras från 1 till V)