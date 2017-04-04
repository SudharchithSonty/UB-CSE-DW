library(RJDBC)
library(rJava)

jdbcDriver <- JDBC(driverClass="oracle.jdbc.OracleDriver", classPath="/Users/Admin/Desktop/DM/ojdbc6.jar")


con <- dbConnect(jdbcDriver, "jdbc:oracle:thin:@//aos.acsu.buffalo.edu:1521/aos.buffalo.edu", "artigupt", "cse562")


assay <- dbGetQuery(con, "SELECT * FROM assay")
gene <- dbGetQuery(con, "SELECT * FROM gene")
clinical_fact <- dbGetQuery(con, "SELECT * FROM CLINICAL_FACT")
cluster1 <- dbGetQuery(con, "SELECT * FROM CLUSTER1")
disease <- dbGetQuery(con, "SELECT * FROM DISEASE")
domain <- dbGetQuery(con, "SELECT * FROM domain")
drug <- dbGetQuery(con, "SELECT * FROM DRUG")
experiment_fact <- dbGetQuery(con, "SELECT * FROM experiment_fact")
experiment <- dbGetQuery(con, "SELECT * FROM experiment")
gene_fact <- dbGetQuery(con, "SELECT * FROM GENE_FACT")
go <- dbGetQuery(con, "SELECT * FROM GO")
marker <- dbGetQuery(con, "SELECT * FROM MARKER")
measureUnit <- dbGetQuery(con, "SELECT * FROM MEASUREUNIT")
microarray_fact <- dbGetQuery(con, "SELECT * FROM microarray_fact")
norm <- dbGetQuery(con, "SELECT * FROM norm")
patient <- dbGetQuery(con, "SELECT * FROM patient")
person <- dbGetQuery(con, "SELECT * FROM person")
platform <- dbGetQuery(con, "SELECT * FROM platform")
probe <- dbGetQuery(con, "SELECT * FROM probe")
project <- dbGetQuery(con, "SELECT * FROM project")
promoter <- dbGetQuery(con, "SELECT * FROM promoter")
sample_fact <- dbGetQuery(con, "SELECT * FROM sample_fact")
sample <- dbGetQuery(con, "SELECT * FROM sample")
term <- dbGetQuery(con, "SELECT * FROM term")
test_sample <- dbGetQuery(con, "SELECT * FROM test_sample")
test <- dbGetQuery(con, "SELECT * FROM test")
protocol <- dbGetQuery(con, "SELECT * FROM protocol")
publication <- dbGetQuery(con, "SELECT * FROM publication")




Result1 <- dbGetQuery(con, "select patient_count_ALL, patient_count_leukemia, patient_count_tumor from (select count(distinct c.p_id) patient_count_ALL from clinical_fact c, disease d where d.name='ALL' and c.ds_id=to_char(d.ds_id)) a, (select count(distinct c.p_id) patient_count_leukemia from clinical_fact c, disease d
where d.type='leukemia' and c.ds_id=to_char(d.ds_id)) b, (select count(distinct c.p_id) patient_count_tumor from clinical_fact c, disease d where d.description='tumor' and c.ds_id=to_char(d.ds_id)) c")



Result2 <- dbGetQuery(con, "select distinct a.type
from drug a,
                      (select distinct c.p_id, c.dr_id 
                      from clinical_fact c, disease d
                      where d.description='tumor'
                      and c.ds_id=to_char(d.ds_id)) b
                      where to_char(a.dr_id)=b.dr_id")






Result3 <- dbGetQuery(con, "select distinct s_id, exp from microarray_fact where
                      pb_id in 
                      (select pb_id from probe p
                       where u_id in 
                       (select u_id from gene_fact g where cl_id='2'))
                      and mu_id='1'
                      and s_id in (select  distinct s_id
                                   from clinical_fact cf
                                   where p_id in (
                                     select  distinct p_id
                                     from clinical_fact c, disease d
                                     where d.name='ALL'
                                     and c.ds_id=to_char(d.ds_id))
                                   and s_id != 'null')")



t_stat <- t.test(e1,e2)