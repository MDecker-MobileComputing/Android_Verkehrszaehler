

-- Zähler für KFZs ohne Bei-/Mitfahrer um 1 erhöhen.
UPDATE zaehler 
   SET anzahl = anzahl + 1
 WHERE name = 'KFZ_ALLEINE';
       

-- Zähler für KFZs mit Bei-/Mitfahrer um 1 erhöhen.
UPDATE zaehler 
   SET anzahl = anzahl + 1
 WHERE name = 'KFZ_MITFAHRER';


-- Zähler für LKWs um 1 erhöhen.
UPDATE zaehler 
   SET anzahl = anzahl + 1
 WHERE name = 'LKW';
