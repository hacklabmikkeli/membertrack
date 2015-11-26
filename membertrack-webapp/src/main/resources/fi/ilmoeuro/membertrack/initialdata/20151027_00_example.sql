INSERT INTO "PUBLIC"."PERSON" ("id","full_name","email") VALUES
    (1,'Kaseem Dickson','sed.dolor@lacusUt.co.uk'),
    (2,'Wynne Chandler','cursus.luctus@Namnullamagna.net'),
    (3,'Jolene Clarke','hendrerit.Donec@lacusCras.ca'),
    (4,'Olympia Ward','placerat@rutrumurna.net'),
    (5,'Melissa Lara','ipsum.non@ut.com'),
    (6,'Emily Clayton','nunc@Curabiturvel.co.uk'),
    (7,'Alma Garrett','nonummy@nonleo.ca'),
    (8,'William Aguirre','odio.vel@disparturientmontes.edu'),
    (9,'Maryam Carrillo','elementum.dui.quis@duisemper.edu'),
    (10,'Lance Ball','sem.molestie.sodales@pedemalesuada.org'),
    (11,'Hamilton Fowler','nec@Praesent.co.uk'),
    (12,'Thor Willis','sem@dolorNulla.ca'),
    (13,'Mira Raymond','dis.parturient.montes@vestibulum.org'),
    (14,'Xena Guy','Sed@urnanec.net'),
    (15,'Blake Page','feugiat.placerat@ullamcorpervelit.ca'),
    (16,'Camille Calhoun','nostra@faucibusutnulla.co.uk'),
    (17,'Lee Coleman','leo@eleifendnunc.edu'),
    (18,'Martin Scott','lorem.lorem@dictum.edu'),
    (19,'Phyllis Petty','elit.Curabitur.sed@magnaaneque.net'),
    (20,'Tyrone Gill','vehicula.aliquet.libero@elementumloremut.org'),
    (21,'Gavin Newton','tellus.Suspendisse.sed@lectus.ca'),
    (22,'Aimee Herman','est.mollis.non@vitaealiquam.com'),
    (23,'Roary Little','turpis.Aliquam.adipiscing@mattisCras.com'),
    (24,'Eliana Blankenship','Curabitur.consequat.lectus@nunc.edu'),
    (25,'Cooper Decker','placerat.velit@in.net'),
    (26,'Larissa Schroeder','velit.Pellentesque.ultricies@egetvolutpat.edu'),
    (27,'Heidi Mcknight','Aenean.egestas.hendrerit@luctusCurabituregestas.net'),
    (28,'Jasper Mcmillan','amet@nec.ca'),
    (29,'Conan Navarro','Duis.elementum.dui@ligula.net'),
    (30,'Flynn Horn','eu@velpedeblandit.ca'),
    (31,'Suki Robbins','Suspendisse.sagittis@Aliquam.edu'),
    (32,'Kirby Fields','neque@musAenean.com'),
    (33,'Dominic Chase','ornare.facilisis@veliteusem.ca'),
    (34,'Cody Rowe','sit@hendreritDonecporttitor.ca'),
    (35,'Tarik Bullock','mi.lacinia@natoquepenatibus.edu'),
    (36,'Camille Newman','Sed@Integer.net'),
    (37,'Raymond Allison','ligula.eu.enim@famesac.org'),
    (38,'Raymond Bartlett','posuere@intempuseu.co.uk'),
    (39,'Cody Grant','consectetuer.euismod@arcuvel.com'),
    (40,'Julie Garrett','Phasellus.fermentum.convallis@placeratorcilacus.co.uk'),
    (41,'Zena Kerr','ridiculus.mus.Proin@ornarelectusante.edu'),
    (42,'Mariko Prince','erat@Mauris.net'),
    (43,'Miriam Massey','interdum@erosProin.com'),
    (44,'Andrew Myers','penatibus.et@tortornibh.ca'),
    (45,'Linda Black','venenatis@Nam.edu'),
    (46,'Larissa Sparks','libero@porttitor.com'),
    (47,'Hanna Foreman','luctus.vulputate@nonmassanon.co.uk'),
    (48,'Brenna Wade','semper.et@ut.com'),
    (49,'Drew Noel','ipsum.dolor.sit@at.com'),
    (50,'Connor Horton','cursus.in.hendrerit@dolorFuscefeugiat.com'),
    (51,'Administrator', 'admin@example.com');

INSERT INTO "PUBLIC"."ACCOUNT" ("person_id", "hash", "salt") VALUES
    (51, 'a90df67816fd16f347f5d8b9d7ee55fb', 'admin');

INSERT INTO "PUBLIC"."PHONE_NUMBER" ("person_id","phone_number") VALUES
    (25,'+35844095801'),
    (17,'+35844289369'),
    (29,'+35844776097'),
    (28,'+35844539786'),
    (19,'+35844114154'),
    (30,'+35844034160'),
    (29,'+35844315909'),
    (13,'+35844593639'),
    (20,'+35844194151'),
    (17,'+35844275856'),
    (48,'+35844665398'),
    (25,'+35844232296'),
    (2,'+35844260863'),
    (14,'+35844176790'),
    (30,'+35844850060'),
    (25,'+35844815355'),
    (11,'+35844676259'),
    (39,'+35844874595'),
    (49,'+35844170103'),
    (25,'+35844045721'),
    (13,'+35844009285'),
    (13,'+35844291404'),
    (25,'+35844665034'),
    (21,'+35844141480'),
    (43,'+35844704197'),
    (46,'+35844005998'),
    (19,'+35844872645'),
    (8,'+35844110742'),
    (18,'+35844070563'),
    (30,'+35844965645'),
    (15,'+35844423202'),
    (39,'+35844202767'),
    (5,'+35844396549'),
    (27,'+35844883218'),
    (1,'+35844586148'),
    (32,'+35844096965'),
    (40,'+35844213403'),
    (22,'+35844604225'),
    (19,'+35844052065'),
    (28,'+35844575876'),
    (26,'+35844429245'),
    (16,'+35844689291'),
    (45,'+35844539613'),
    (38,'+35844149130'),
    (12,'+35844818520'),
    (3,'+35844670550'),
    (47,'+35844998254'),
    (37,'+35844266253'),
    (28,'+35844705179'),
    (32,'+35844303627');


INSERT INTO
    "PUBLIC"."SERVICE" ("id", "title", "description")
VALUES
    (1, 'Jäsenmaksut', 'Jäsenmaksut'),
    (2, 'Tilankäyttö', 'Tilankäyttomaksut');


INSERT INTO "SUBSCRIPTION_PERIOD" ("service_id","person_id","start_time","length","payment") VALUES
    (1,13,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (1,14,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (1,16,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (1,1,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (1,20,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (1,21,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (1,23,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (1,25,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (1,27,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (1,28,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (1,2,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (1,30,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (1,34,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (1,36,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (1,37,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (1,42,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (1,43,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (1,48,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (1,4,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (1,5,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (2,14,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (2,15,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (2,17,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (2,18,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (2,21,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (2,22,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (2,23,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (2,24,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (2,25,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (2,28,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (2,29,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (2,30,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (2,31,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (2,34,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (2,35,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (2,36,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (2,37,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (2,39,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (2,41,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (2,46,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (2,4,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (2,50,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (2,5,{ts '2015-1-1 00:00:00.00'},364*86400,2000),
    (2,6,{ts '2015-1-1 00:00:00.00'},364*86400,2000);
