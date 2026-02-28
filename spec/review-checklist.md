<!--
Prompt der dannede denne fil:
"dan en review-checklist.md og placer den i spec/ øverst lav en kommentar med denne prompt der har dannet filen. indsæt de bedste review punkter for owasp, kode kvalitet og ensartet formatering. samt tilføj de ting du mener jeg har glemt"
-->

# Review Checklist

Brug denne checklist ved PR/code review. Marker `OK`, `N/A` eller tilføj en note.

## 1. OWASP / Security
- [ ] Input valideres server-side (ikke kun klient-side).
- [ ] Output encodes korrekt (HTML/JSON/URL/SQL context).
- [ ] Ingen SQL/command injection-risiko (parametriserede queries, ingen string-konkatenation).
- [ ] AuthN/AuthZ er korrekt håndhævet for alle endpoints.
- [ ] Secrets/tokens/passwords ligger ikke i kode, logs eller commits.
- [ ] Fejlmeddelelser lækker ikke stacktraces eller følsomme data.
- [ ] Sikker session-håndtering (timeout, rotation, invalidation).
- [ ] Dependencies er scannet for kendte sårbarheder (SCA).
- [ ] Sikker transport anvendes (TLS, ingen usikre protokoller).
- [ ] Logging/audit dækker sikkerhedshændelser uden at logge PII/secrets.

## 2. Kodekvalitet
- [ ] Løsningen matcher krav/use-cases og ændrer ikke utilsigtet adfærd.
- [ ] Små, fokuserede ændringer uden unødig refaktorering.
- [ ] Navngivning er tydelig og konsistent med domænet.
- [ ] Kompleksitet er rimelig (ingen unødigt dybe if/loops, methoder har klart ansvar).
- [ ] Fejlhåndtering er eksplicit og konsistent.
- [ ] Edge cases er dækket (null/tom input, grænseværdier, fejlflow).
- [ ] Tests dækker ny/ændret adfærd inkl. negative cases.
- [ ] Ingen dead code, TODO-gæld uden ticket/reference.
- [ ] Performance-risici vurderet (N+1, unødige allokeringer, store loops).
- [ ] Concurrency-tråde/synkronisering er sikker hvor relevant.

## 3. Ensartet Formatering
- [ ] Formatteringsregler følges konsekvent i hele diffen.
- [ ] Import-rækkefølge, linjelængde og whitespace er ensartet.
- [ ] Ingen blanding af navnekonventioner i samme modul.
- [ ] Kommentarer beskriver hvorfor (ikke kun hvad).
- [ ] Ingen støjende reformat af uberørte filer.

## 4. Arkitektur & Drift (ofte glemt)
- [ ] Ændringen respekterer lagdeling/arkitekturgrænser.
- [ ] API-kontrakter (request/response/fejlkoder) er stabile eller versioneret.
- [ ] Backward compatibility vurderet for eksisterende klienter/data.
- [ ] Migration/rollback-plan findes ved data- eller schemaændringer.
- [ ] Observability er på plads (meningsfulde logs, metrics, tracing).
- [ ] Feature flags/toggles bruges ved risikable releases.
- [ ] Dokumentation opdateret (README/spec/ADR) når adfærd ændres.

## 5. CI/CD & Leverance (ofte glemt)
- [ ] Build/test/lint er grøn lokalt og i CI.
- [ ] Reproducerbar build (låste versioner, ingen skjulte miljøafhængigheder).
- [ ] Release-notes/changelog er opdateret ved brugerrettede ændringer.
- [ ] Licens/compliance vurderet for nye dependencies.
- [ ] Ingen ændring af CI/secrets/infrastruktur uden eksplicit godkendelse.

## 6. Review-afslutning
- [ ] Kritiske issues er blokerende før merge.
- [ ] Medium/lav issues er oprettet som opgaver med prioritet.
- [ ] Endelig beslutning: Approve / Request changes.
