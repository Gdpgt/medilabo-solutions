// MongoDB init script — runs on first container startup (when /data/db is empty).
// Executed by mongosh with root credentials provided via MONGO_INITDB_ROOT_USERNAME / MONGO_INITDB_ROOT_PASSWORD.

db = db.getSiblingDB(process.env.MONGO_INITDB_DATABASE);

// Application-specific user (not root) used by note-praticien-service to connect.
db.createUser({
    user: process.env.MONGODB_USER,
    pwd: process.env.MONGODB_PASSWORD,
    roles: [{ role: "readWrite", db: process.env.MONGO_INITDB_DATABASE }]
});

db.createCollection("notes_praticien");

db.notes_praticien.insertMany([
    {
        idPatient: 1,
        nomPatient: "TestNone",
        note: "Le patient déclare qu'il 'se sent très bien'\nPoids égal ou inférieur au poids recommandé",
        dateCreation: ISODate("2025-03-12T09:30:00Z"),
        version: 0
    },
    {
        idPatient: 2,
        nomPatient: "TestBorderline",
        note: "Le patient déclare qu'il ressent beaucoup de stress au travail\nIl se plaint également que son audition est anormale dernièrement",
        dateCreation: ISODate("2024-11-05T14:15:00Z"),
        version: 0
    },
    {
        idPatient: 2,
        nomPatient: "TestBorderline",
        note: "Le patient déclare avoir fait une réaction aux médicaments au cours des 3 derniers mois\nIl remarque également que son audition continue d'être anormale",
        dateCreation: ISODate("2025-02-18T10:00:00Z"),
        version: 0
    },
    {
        idPatient: 3,
        nomPatient: "TestInDanger",
        note: "Le patient déclare qu'il fume depuis peu",
        dateCreation: ISODate("2024-09-22T11:45:00Z"),
        version: 0
    },
    {
        idPatient: 3,
        nomPatient: "TestInDanger",
        note: "Le patient déclare qu'il est fumeur et qu'il a cessé de fumer l'année dernière\nIl se plaint également de crises d'apnée respiratoire anormales\nTests de laboratoire indiquant un taux de cholestérol LDL élevé",
        dateCreation: ISODate("2025-04-08T16:20:00Z"),
        version: 0
    },
    {
        idPatient: 4,
        nomPatient: "TestEarlyOnset",
        note: "Le patient déclare qu'il lui est devenu difficile de monter les escaliers\nIl se plaint également d'être essoufflé\nTests de laboratoire indiquant que les anticorps sont élevés\nRéaction aux médicaments",
        dateCreation: ISODate("2024-07-14T08:50:00Z"),
        version: 0
    },
    {
        idPatient: 4,
        nomPatient: "TestEarlyOnset",
        note: "Le patient déclare qu'il a mal au dos lorsqu'il reste assis pendant longtemps",
        dateCreation: ISODate("2024-10-30T13:10:00Z"),
        version: 0
    },
    {
        idPatient: 4,
        nomPatient: "TestEarlyOnset",
        note: "Le patient déclare avoir commencé à fumer depuis peu\nHémoglobine A1C supérieure au niveau recommandé",
        dateCreation: ISODate("2025-01-21T15:35:00Z"),
        version: 0
    },
    {
        idPatient: 4,
        nomPatient: "TestEarlyOnset",
        note: "Taille, Poids, Cholestérol, Vertige et Réaction",
        dateCreation: ISODate("2025-04-02T09:00:00Z"),
        version: 0
    }
]);
