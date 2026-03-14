<!--
Prompt der dannede denne fil:
"dan en review-checklist.md og placer den i spec/ øverst lav en kommentar med denne prompt der har dannet filen. indsæt de bedste review punkter for owasp, kode kvalitet, herunder concurrency bugs, ofte bugs i kode og ensartet navngivning og ensartet formatering. samt tilføj de ting du mener jeg har glemt"
-->

# Review Checklist

Brug listen ved PR/code review. Notér fund pr. punkt.

## 1. OWASP / Sikkerhed
- Inputvalidering på server-side for alle felter og boundary values.
- Output encoding i korrekt context (HTML/JSON/URL/SQL).
- Ingen injection-risici (SQL/NoSQL/command/template); brug parameterisering.
- Korrekt AuthN/AuthZ på alle endpoints, inkl. "admin only" paths.
- Secrets/tokens/passwords aldrig i kode, logs eller commits.
- Fejlbeskeder må ikke lække stacktrace, intern struktur eller følsomme data.
- Dependency scanning og sårbarheder vurderet før merge.
- Sikker session/cookie-policy (Secure, HttpOnly, SameSite, rotation).

## 2. Kodekvalitet
- Ændringen matcher krav/use-case og introducerer ikke sideeffekter.
- Små, fokuserede changes; ingen unødig refactor i samme PR.
- Kode er læsbar: tydelige navne, små metoder, lav kompleksitet.
- Fejlhåndtering er konsekvent og meningsfuld.
- Tests dækker ny adfærd + negative cases + edge cases.
- Ingen dead code, debug-rester eller ubegrundede TODOs.

## 3. Concurrency Bugs (kritisk)
- Ingen data races på shared mutable state.
- Korrekt synkronisering/locks/atomics; ingen "check-then-act" race.
- Thread-safe collections bruges hvor nødvendigt.
- Ingen potentiale for deadlock/livelock/starvation.
- Async fejl håndteres; exceptions i baggrundstråde tabes ikke.
- Cancellation/timeouts håndteres korrekt i async flows.
- Visibility-garantier er på plads (happens-before, volatile, final init).

## 4. Typiske kodebugs
- Null-håndtering er robust (ingen skjulte NPE-risici).
- Off-by-one/grænsefejl i loops, datoer og indekser.
- Forkert sammenligning (`==` vs `.equals`) undgås.
- Forkert tidszone/tidstype undgås (Instant/LocalDateTime/ZonedDateTime bevidst valgt).
- Overløb/underløb vurderet for numeriske beregninger.
- Ressourcer lukkes korrekt (streams, filer, forbindelser).

## 5. Ensartet navngivning
- Domænebegreber bruges konsekvent i kode, tests og API.
- Samme koncept har samme navn på tværs af lag.
- Klasse-, metode- og variabelnavne følger projektets konvention.
- Ingen blanding af sprog/terminologi uden klar grund.

## 6. Ensartet formatering
- Formatteringsregler følges i hele diffen.
- Import-rækkefølge, linjelængde og whitespace er konsistente.
- Ingen massereformat af filer uden funktionel ændring.
- Kommentarer forklarer hvorfor, ikke blot hvad.

## 7. Ofte glemte områder
- API/backward compatibility vurderet.
- Migration/rollback-plan ved data- eller schemaændringer.
- Observability: relevante logs, metrics og tracing er tilføjet/opdateret.
- Performance: hotspots, N+1, unødig allokering og store loops vurderet.
- Feature flags/release-strategi ved risikable ændringer.
- Dokumentation (README/spec/ADR/changelog) opdateret når adfærd ændres.
- Licens/compliance vurderet for nye dependencies.
