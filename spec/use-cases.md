# Use Cases

## Aktører
- Primær:
- Sekundær:

## Use case liste
| ID    | Titel  | Aktør | Mål                           |
|-------|--------|-------|-------------------------------|
| UC-01 | beregn | koder | beregne et resultat via input |
 | UC-02 | list | koder | visning af event|

## Use case template
### UC-01: tilføj event og beregn
- Formål: vi skal lave en beregn api via to tidslinier  
- Prækonditioner:
- Trigger:
- Hovedflow:
en "tidslinie" kan tilføjes et event med "addEvent()"
en tidslinie kan beregnes, "Calculate()" som finder alle eventinput som ikke har et beregningsresultat og beregner denne.
- beregning foregår ved at finde den ældste eventinput i realtid der ikke har et beregningsresultat og beregne det. 


Dan en testmetode "init" der opretter en person, en tidslinie og beregner en tom tidslinie

  1.
  2.
- Alternative flows:
- Fejlflow:
- Postkonditioner:
- Acceptkriterier:

## Ikke-funktionelle krav (kort)
- Performance:
- Sikkerhed:
- Observability:


### UC-02: visning
visning tager et personid
viser en liste af events sorteret i valørtid
event vises som eventnavn, samt nyeste eventinput med nyeste calculationresult


- dan en test metode "en_indbetaling_print" der danner en person, tidslinie med en indbetaling 100 kr 1/1 2026 på 100 kr og udskriver ham efter beregn
- dan en test metode "to_indbetaling_print" der danner en person med en indbetaling 100 kr 1/1 2026 på 100 kr og 200 kr 1/2/26 og udskriver ham efter beregn

