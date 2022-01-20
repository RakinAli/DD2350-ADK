## Heuristik för rollbesättningsproblemet

Målen för labb 5 är att du ska

- öva på att attackera ett problem som inte kan lösas optimalt
- implementera en heuristik för ett NP-svårt problem

Du ska välja att implementera valfri heuristik som löser konstruktionsproblemet: Vilka skådespelare ska ha vilka roller för att lösa rollbesättningsinstansen med så få skådespelare som möjligt? 

Indataformatet för rollbesättningsproblemet är detsamma som i labb 4. 

Divorna är 1 och 2.

### Utdataformat: 
Rad ett: antal skådespelare som fått roller 

En rad för varje skådespelare (som fått roller) med skådespelarens nummer, antalet roller skådespelaren tilldelats samt numren på dessa roller

Problemet ska lösas enligt villkoren som specificerats för rollbesättningsproblemet, dvs divorna måste vara med men får inte mötas, ingen roll får spelas av flera personer, och ingen skådespelare får spela mot sig själv i någon scen. Bättre heuristik (dvs färre skådespelare) ger bättre betyg. Endast lösbara instanser kommer att ges som indata, men för att heuristiken i polynomisk tid säkert ska kunna hitta en lösning så är det tillåtet att använda högst n-1 särskilda superskådisar med nummer k+1, k+2, ... Varje superskådis kan spela vilken roll som helst, men kan bara spela en enda roll.