# JavTerra - Database application
***
## Jméno a kontakt na autora
Author: Daniel Brus

Email: brud06@vse.cz
## Popis aplikace
Aplikace je inspirována hrou Shakes and Fidget. Uživatelé se mohou registrovat.
Po registraci se mohou ke svému účtu přihlásit a vytvářet herní postavy, se kterými se jde následně
přihlásit do herního světa (zatím lze pouze vidět seznam postav na daném herním světě). Dalšími funkcemi
jsou správa uživatelského účtu - změny v údajích a mazání. Uživatelské role se dělí na běžné uživatle a adminy.
Admini mohou vytvářet herní světy, editovat je a zároveň je také mazat. Jejich role je správa těchto herních světů.

## Relační databáze
Aplikace využívá relační databázi H2.

## Kompilace a build
Projekt lze zkompilovat a sestavit za pomocí nástorje Maven

## Nedořešené problémy
V současné době se může uživatel přihlásit najednou z více zařízení, což není vyloženě problém.
Autor zvažoval nastavení flagu pomocí sloupce boolean "ACTIVE".
Bohužel toto řešení mělo nevýhodu, že pokud se uživateli nepovedlo správně odhlásit,
což mohlo být způsobeno například pádem aplikace, zůstal "locknutý" a ke svému účtu se již nemohl přihlásit.
Autor proto zvolil řešení, kdy povoluje současné přihlášení jednoho uživatele z více zařízeních.

## Odchylky oproti zadání
Žádné