INSERT INTO onglet(id, idComposant, nom, description, iframe, src, ordre)
    VALUES (nextval('onglet_id_seq'), 'liste', 'Listes', 'Recherche de mots dans les listes', false, '/listes.zul', 1);

INSERT INTO onglet(id, idComposant, nom, description, iframe, src, ordre)
    VALUES (nextval('onglet_id_seq'), 'contexte', 'Contextes', 'Recherche de mots dans les contextes', false, '/contextes.zul', 2);
