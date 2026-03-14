# GENERATION Rules

Disse regler gælder for al fremtidig AI-assisteret kodegenerering/ændring.

## Ændringsprincipper
1. Lav små, fokuserede diffs pr. opgave.
2. Rør kun filer der er nødvendige for opgaven.
3. Bevar eksisterende navngivning, struktur og stil.
4. Ingen brede refactors uden eksplicit godkendelse.

## Respekt for manuelle ændringer
1. Overskriv aldrig kendte manuelle ændringer.
2. Hvis der er konflikt, stop og foreslå minimal merge-strategi.
3. Bevar kommentarer/notes medmindre de er åbenlyst forældede og aftalt.


## Ejerskab af kode (must-have)

### AI-owned (må oprettes/overskrives)
- `src/generated/**`
    - Alt her må generatoren oprette, ændre og overskrive.
    - Regenerering skal så vidt muligt kun påvirke `src/generated/**`.
    - Hvis noget kræver speciallogik, skal det lægges i `src/manual/**` og ikke i generated.

### Human-owned (må IKKE ændres)
- .git mappen må ALDRIG ændres!!
- `src/**` (hele mappen er read-only for generatoren, pånær generated)
- `manual/**` (hvis den findes)
- migrations/historikfiler (uden eksplicit tilladelse)
- CI/CD og secrets-konfiguration (uden eksplicit tilladelse)


## Ændringer udenfor src/generated
Generatoren må kun ændre filer udenfor `src/generated/**` når det er nødvendigt for integration
(fx registrere routes/DI/export), og kun som **minimale patches** (ingen mass rewrite, ingen reformat).

## Kvalitetskrav
1. Tilføj/opdatér tests kun hvor adfærden ændres.
2. Undgå unødvendige dependencies.
3. Hold public API stabilt medmindre ændring er aftalt.
4. Dokumentér trade-offs kort i PR/commit-beskrivelse.
5. lav nye tests ved ny funktionalitet

## Arbejdsflow
1. Beskriv plan før større ændringer.
2. Implementér i små trin.
3. Verificér med relevante checks/tests.
4. Opsummér præcist hvad der blev ændret.
