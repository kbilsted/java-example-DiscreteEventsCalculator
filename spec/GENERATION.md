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

## Beskyttede områder
1. Ingen overskrivning af `manual/` (hele mappen er read-only for generatoren).
2. Ingen omskrivning af migrations/historikfiler uden eksplicit tilladelse.
3. Ingen automatisk ændring af CI/CD eller secrets-konfiguration.

## Kvalitetskrav
1. Tilføj/opdatér tests kun hvor adfærden ændres.
2. Undgå unødvendige dependencies.
3. Hold public API stabilt medmindre ændring er aftalt.
4. Dokumentér trade-offs kort i PR/commit-beskrivelse.

## Arbejdsflow
1. Beskriv plan før større ændringer.
2. Implementér i små trin.
3. Verificér med relevante checks/tests.
4. Opsummér præcist hvad der blev ændret.
