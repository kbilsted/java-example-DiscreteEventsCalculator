# Domain

## Formål
beregninger af tidslinier som indeholder events på to-dimensionel tidslinie

## Scope
- In scope:
  - console
  - unittest til opstilling af scenarier og validering
- Out of scope:
  - databaser
  - gui

## Centrale begreber
| Begreb | Beskrivelse | Noter |
|---|---|---|
|  |  |  |

## Entiteter og relationer
- Entitet person:
  - navn string
  - personid int 

- Entitet db:
  - dictionary personid, tidslinie

- Entitet tidslinie:
  - max Generation int
  - seneste ændring dato
  - personid

- Entitet event:
  - event id (unikt int) 
  - "eventnavn",
  -  "personid" int
  - valørtid
  - realtid
  -  "eventinput" liste
  - "BeregningsResultat " liste
  
et event kan ikke eksistere uden mindst 1 eventinput

- Relationer:
    - event input
    - calculation result


- Entitet abs eventinput:
abstrakt type alle eventput arver fra
  - eventinput id
  - eventinput realtid

  
- Entitet eventinput indbetaling:
  - beløb int

- Entitet Beregnings Resultat:
  - realtid
  - generationid
  - event input id 
  - índbetalinger  - dictionary pr år udfra valørdato
- Relationer: Tidslinie

beregning af et event input resulterer i en calculationresult. denne har en realtime felt for beregningen samt et stigende generationsnummer

realtid forklarer oprettelsestidspunkt. valørtid er den juridiske dato 



## Domæneregler
1. systemet er read og add. aldrig mutate. ændring til et event er et nyt eventinput
2.
3.

## Events/tilstande
- Event:
- Trigger:
- Effekt:

## Åbne spørgsmål
- hvorledes slettes et event 
