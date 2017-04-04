library(RJDBC)
library(shiny)
library(rJava)

# Define UI for application that draws a histogram
ui <- shinyUI(fluidPage(
  
  # Application title
  titlePanel("Data Warehousing and OLAP Operations for Clinical Data"),
  
  # Sidebar with a slider input for number of bins 
  sidebarLayout(
    
    sidebarPanel(
      selectInput("selection", "Choose a Query:", choices = queries),
      
      
      actionButton("update", "Run Query" ),
      
      br(),
      br(),
      br(),
      
      textAreaInput("newquery", "New Query", placeholder =  "Please enter query", width = "250px", resize = NULL),    
      submitButton(text = "Run Query", icon = icon("submit"), width = NULL),
      
      div(style="display:inline-block",
          uiOutput('resetable_input'),
          tags$hr(),
          actionButton("reset", "Reset Input")),
      br(),
    
      br()
    ),
    
    
    # Show the query output
    mainPanel(
      tabsetPanel(
        tabPanel("Data", dataTableOutput("table"))
      )
    )
  )))


# Define server logic required to draw a histogram
server <- shinyServer(function(input, output) {
  
  terms <- reactive({
    
    jdbcDriver <- JDBC(driverClass="oracle.jdbc.OracleDriver", classPath="/Users/Admin/Desktop/DataMining/Project1/ojdbc6.jar")
    
    con <- dbConnect(jdbcDriver, "jdbc:oracle:thin:@//aos.acsu.buffalo.edu:1521/aos.buffalo.edu", "artigupt", "cse562")
    
    queries <- c("select patient_count_ALL, patient_count_leukemia, patient_count_tumor from (select count(distinct c.p_id) patient_count_ALL from clinical_fact c, disease d where d.name='ALL' and c.ds_id=to_char(d.ds_id)) a, (select count(distinct c.p_id) patient_count_leukemia from clinical_fact c, disease d
                   where d.type='leukemia' and c.ds_id=to_char(d.ds_id)) b, (select count(distinct c.p_id) patient_count_tumor from clinical_fact c, disease d where d.description='tumor' and c.ds_id=to_char(d.ds_id)) c",
                 
                 "select distinct a.type
                   from drug a,
                   (select distinct c.p_id, c.dr_id
                   from clinical_fact c, disease d
                   where d.description='tumor'
                   and c.ds_id=to_char(d.ds_id)) b
                   where to_char(a.dr_id)=b.dr_id",
                 
                 "select distinct s_id, exp from microarray_fact where
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
    
    names(queries) = c("Query1", "Query2", "Query3") 
    # Change when the "update" button is pressed...
    
    random <- eventReactive(input$update,
                            {
                              runif(input$queries)
                            })
    
    
    
    
  })
  output$table <- renderDataTable({
    
    
    dbGetQuery(con, input$selection) })
  
  
  
})

# Run the application 
shinyApp(ui = ui, server = server)

