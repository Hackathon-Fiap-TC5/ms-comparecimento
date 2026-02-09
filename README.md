# MS Comparecimento SUS

Microserviço responsável por calcular e disponibilizar indicadores de comparecimento e absenteísmo de pacientes do Sistema Único de Saúde (SUS).

## 📋 Índice

- [Visão Geral](#visão-geral)
- [Arquitetura](#arquitetura)
- [Tecnologias](#tecnologias)
- [Pré-requisitos](#pré-requisitos)
- [Instalação e Configuração](#instalação-e-configuração)
- [Executando a Aplicação](#executando-a-aplicação)
- [API Documentation](#api-documentation)
- [Lógica de Negócio](#lógica-de-negócio)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Banco de Dados](#banco-de-dados)
- [Mensageria](#mensageria)
- [Testes](#testes)
- [Deploy](#deploy)
- [Arquitetura Detalhada](#arquitetura-detalhada)
- [Contribuindo](#contribuindo)
- [Troubleshooting](#troubleshooting)

## 🎯 Visão Geral

O **MS Comparecimento** é um microserviço desenvolvido para o Hackathon da FIAP (Módulo 5) que calcula e gerencia índices de comparecimento de pacientes (ICC - Índice de Comparecimento do Cliente) no contexto do SUS.

### Funcionalidades Principais

- **Cálculo Automático de ICC**: Processa eventos de agendamento recebidos via mensageria e calcula o índice de comparecimento de cada paciente
- **Classificação de Pacientes**: Classifica pacientes em categorias baseadas no ICC (de "Muito Confiável" até "Realocação Imediata")
- **Consultas Individuais**: Permite consultar o índice de comparecimento de um paciente específico via CNS (Cartão Nacional de Saúde)
- **Relatórios Gerenciais**: Gera relatórios consolidados de absenteísmo por período

### Índice de Comparecimento (ICC)

O ICC é calculado usando um algoritmo que considera:
- Histórico de comparecimentos do paciente
- Taxa de confirmações
- Taxa de faltas
- Comportamento recente (eventos de agendamento)
- Status de notificações enviadas

O índice varia de 0 a 100, onde valores mais altos indicam maior probabilidade de comparecimento.

## 🏗️ Arquitetura

O projeto segue os princípios de **Clean Architecture** e **Domain-Driven Design (DDD)**, organizando o código em camadas bem definidas:

```
ms-comparecimento/
├── domain/              # Camada de domínio (regras de negócio puras)
│   ├── enuns/          # Enumeradores do domínio
│   ├── exception/      # Exceções de domínio
│   └── model/          # Entidades de domínio
├── application/         # Camada de aplicação (casos de uso)
│   ├── gateway/        # Interfaces de portas de saída
│   └── usecase/        # Casos de uso da aplicação
├── infrastructure/      # Camada de infraestrutura
│   ├── config/         # Configurações (RabbitMQ, Beans)
│   └── database/       # Implementações de persistência
└── entrypoint/          # Camada de entrada (controllers, listeners)
    ├── controllers/    # REST Controllers
    └── listeners/      # Message Listeners (RabbitMQ)
```

### Fluxo de Dados

1. **Eventos de Agendamento**: Recebidos via RabbitMQ na fila `comparecimento.queue`
2. **Processamento**: O `ComparecimentoConsumer` processa os eventos e chama o caso de uso de cálculo
3. **Cálculo**: O `CalculaComparecimentoUseCase` calcula o novo ICC e atualiza os dados do paciente
4. **Persistência**: Dados são salvos no banco MySQL através do `PacienteGateway`
5. **Consultas**: APIs REST permitem consultar dados individuais ou gerar relatórios

## 🛠️ Tecnologias

- **Java 21**: Linguagem de programação
- **Spring Boot 4.0.2**: Framework principal
- **Spring Data JPA**: Persistência de dados
- **MySQL**: Banco de dados relacional
- **RabbitMQ**: Mensageria para eventos assíncronos
- **MapStruct**: Mapeamento entre objetos
- **Lombok**: Redução de boilerplate
- **OpenAPI/Swagger**: Documentação de API
- **JUnit 5**: Framework de testes
- **Mockito**: Mocking em testes
- **Testcontainers**: Testes de integração com containers
- **JaCoCo**: Cobertura de código

## 📦 Pré-requisitos

- Java 21 ou superior
- Maven 3.9+
- MySQL 8.0+ (ou acesso a CloudSQL)
- RabbitMQ 3.x+
- Docker (opcional, para testes com containers)

## ⚙️ Instalação e Configuração

### 1. Clone o repositório

```bash
git clone <repository-url>
cd ms-comparecimento
```

### 2. Configure o banco de dados

Execute o script SQL para criar o banco e tabelas:

```bash
mysql -u root -p < ddl/create_database.sql
```

Ou execute manualmente o conteúdo de `ddl/create_database.sql` no seu MySQL.

### 3. Configure as variáveis de ambiente

Crie um arquivo `application-local.properties` ou configure as seguintes variáveis de ambiente:

```properties
# Database
DB_USERNAME=root
DB_PASSWORD=sua_senha

# RabbitMQ
RABBIT_HOST=localhost
RABBIT_PORT=5672
RABBIT_USERNAME=guest
RABBIT_PASSWORD=guest

# Server Port (opcional)
PORT=8080
```

### 4. Configure o RabbitMQ

Certifique-se de que o RabbitMQ está rodando e que a fila `comparecimento.queue` e o exchange `agendamento.exchange` estão configurados. A aplicação criará automaticamente esses recursos na inicialização.

## 🚀 Executando a Aplicação

### Modo Desenvolvimento

```bash
mvn spring-boot:run
```

Ou usando o wrapper Maven:

```bash
./mvnw spring-boot:run
```

### Build e Execução

```bash
# Build
mvn clean install

# Executar JAR
java -jar target/ms-comparecimento-1.0.0-SNAPSHOT.jar
```

### Docker

```bash
# Build da imagem
docker build -t ms-comparecimento:latest .

# Executar container
docker run -p 8080:8080 \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=senha \
  -e RABBIT_HOST=rabbitmq \
  -e RABBIT_PORT=5672 \
  ms-comparecimento:latest
```

A aplicação estará disponível em `http://localhost:8080`

## 📚 API Documentation

A documentação da API está disponível via Swagger UI quando a aplicação está rodando:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Endpoints Principais

#### 1. Consultar Índice de Comparecimento do Paciente

```http
GET /v1/pacientes/indice-comparecimento?cns={cns}
```

**Parâmetros:**
- `cns` (query, obrigatório): Cartão Nacional de Saúde do paciente

**Resposta 200:**
```json
{
  "cns": "89800112345678",
  "icc": 72.5,
  "classificacao": "COMPARECIMENTO_PROVAVEL",
  "totalComparecimentos": 8,
  "totalAgendamentos": 11,
  "totalFaltas": 3,
  "totalCancelamentos": 0,
  "totalConfirmacoes": 5,
  "ultimaAtualizacao": "2026-01-20T14:35:00-03:00"
}
```

**Resposta 404:**
```json
{
  "message": "Paciente não encontrado"
}
```

#### 2. Relatório de Absenteísmo por Período

```http
GET /v1/relatorios/absenteismo?dataInicio={dataInicio}&dataFim={dataFim}
```

**Parâmetros:**
- `dataInicio` (query, obrigatório): Data de início do período (formato: YYYY-MM-DD)
- `dataFim` (query, obrigatório): Data de fim do período (formato: YYYY-MM-DD)

**Resposta 200:**
```json
{
  "periodo": {
    "dataInicio": "2026-01-01",
    "dataFim": "2026-01-31"
  },
  "totalPessoas": 320,
  "iccMedio": 67.4,
  "totalConsultas": 1200,
  "totalFaltas": 340,
  "taxaAbsenteismo": 28.3,
  "dataGeracao": "2026-02-01T10:00:00-03:00"
}
```

**Resposta 400:**
```json
{
  "message": "Período inválido"
}
```

## 🧮 Lógica de Negócio

### Cálculo do ICC (Índice de Comparecimento do Cliente)

O ICC é calculado através de um algoritmo que combina múltiplos fatores:

#### 1. Taxas Base

- **Taxa de Comparecimento**: `(totalComparecimentos + 1) / (totalAgendamentos + 2)`
- **Taxa de Confirmação**: `(totalConfirmacoes + 1) / (totalAgendamentos + 2)`
- **Taxa de Faltas**: `(totalFaltas + 0.5) / (totalAgendamentos + 1.5)`

#### 2. Score Histórico

- **Score de Comparecimento**: Baseado em função sigmoidal da taxa de comparecimento
- **Score de Confirmação**: Baseado em função exponencial da taxa de confirmação
- **Penalidade de Faltas**: Penalização exponencial baseada na taxa de faltas

#### 3. Maturidade do Paciente

A maturidade é calculada como: `min(1.0, log(totalAgendamentos + 1) / log(20))`

Pacientes com mais histórico têm maior "maturidade" e o histórico pesa mais no cálculo.

#### 4. Score do Evento

O evento atual (status da consulta + status da notificação) contribui com um score que varia conforme a combinação:

- **REALIZADO + CONFIRMOU_48H_ANTECEDENCIA**: +4.0
- **FALTA + EXPIRADA**: -5.5
- E outras combinações específicas...

#### 5. Cálculo Final

```java
ICC = (scoreHistorico * maturidade) + (scoreEvento * pesoEvento)
ICC Normalizado = 100 / (1 + exp(-ICC / 3.2))
```

### Classificação de Pacientes

O ICC é classificado em 9 categorias:

| ICC | Classificação |
|-----|---------------|
| ≥ 90 | MUITO_CONFIAVEL |
| ≥ 80 | CONFIAVEL |
| ≥ 70 | COMPARECIMENTO_PROVAVEL |
| ≥ 60 | COMPARECIMENTO_INCERTO |
| ≥ 50 | BAIXA_PROBABILIDADE_DE_COMPARECIMENTO |
| ≥ 40 | PROVAVEL_NAO_COMPARECIMENTO |
| ≥ 30 | CRITICO |
| ≥ 20 | REALOCACAO_POSSIVEL |
| < 20 | REALOCACAO_IMEDIATA |

### Status de Consulta

- **AGENDADO**: Consulta foi agendada
- **CONFIRMADO**: Paciente confirmou presença
- **REALIZADO**: Consulta foi realizada
- **FALTA**: Paciente faltou
- **CANCELADO**: Consulta foi cancelada

### Status de Notificação

- **NAO_ENVIADA**: Notificação não foi enviada
- **ENVIADA**: Notificação enviada
- **ENTREGUE**: Notificação entregue ao paciente
- **CONFIRMOU_48H_ANTECEDENCIA**: Paciente confirmou com 48h de antecedência
- **CONFIRMOU_24H_ANTECEDENCIA**: Paciente confirmou com 24h de antecedência
- **FALHA**: Falha no envio da notificação
- **EXPIRADA**: Notificação expirou

## 📁 Estrutura do Projeto

```
src/main/java/com/fiap/comparecimento/
├── application/
│   ├── gateway/
│   │   └── PacienteGateway.java              # Interface para acesso a dados de pacientes
│   └── usecase/
│       ├── calcula/comparecimento/           # Caso de uso: cálculo de comparecimento
│       ├── pacientes/                        # Caso de uso: consulta de pacientes
│       └── relatorios/                       # Caso de uso: relatórios
├── domain/
│   ├── enuns/                                # Enumeradores do domínio
│   ├── exception/                            # Exceções de domínio
│   └── model/                                # Entidades de domínio
├── entrypoint/
│   ├── controllers/                          # REST Controllers
│   └── listeners/                            # Message Listeners (RabbitMQ)
├── infrastructure/
│   ├── config/                               # Configurações
│   └── database/                             # Implementações de persistência
└── utils/                                     # Utilitários
```

## 🗄️ Banco de Dados

### Schema

A tabela principal é `tb_paciente`:

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `cns` | VARCHAR(15) | Cartão Nacional de Saúde (PK) |
| `icc` | INT | Índice de Comparecimento do Cliente |
| `classificacao` | VARCHAR(50) | Classificação do paciente |
| `total_comparecimentos` | INT | Total de comparecimentos |
| `total_faltas` | INT | Total de faltas |
| `total_confirmacoes` | INT | Total de confirmações |
| `total_cancelamentos` | INT | Total de cancelamentos |
| `total_agendamentos` | INT | Total de agendamentos |
| `ultima_atualizacao` | TIMESTAMP(6) | Data/hora da última atualização |

### Índices

- **PRIMARY KEY**: `cns`
- **INDEX**: `idx_ultima_atualizacao` (para consultas por período)
- **INDEX**: `idx_classificacao` (para filtros por classificação)

## 📨 Mensageria

### RabbitMQ

A aplicação consome eventos de agendamento da fila `comparecimento.queue`.

#### Configuração

- **Exchange**: `agendamento.exchange` (Topic Exchange)
- **Queue**: `comparecimento.queue` (Durable)
- **Routing Key**: `agendamento.key`

#### Formato da Mensagem

```json
{
  "cns": "123456789012345",
  "statusConsulta": "REALIZADO",
  "statusNotificacao": "CONFIRMOU_24H",
  "dataEvento": "2026-01-15T09:30:00-03:00"
}
```

#### Processamento

1. Mensagem é recebida pelo `ComparecimentoConsumer`
2. Convertida para `EventoAgendamentoMessageDomain`
3. Processada pelo `CalculaComparecimentoUseCase`
4. ICC é recalculado e paciente atualizado no banco

### Retry e Acknowledgment

- **Retry automático**: Habilitado com 3 tentativas
- **Intervalo inicial**: 1 segundo
- **Multiplicador**: 2.0
- **Acknowledgment**: Automático

## 🧪 Testes

### Executar Testes

```bash
# Todos os testes
mvn test

# Testes com cobertura
mvn clean test jacoco:report
```

### Cobertura de Código

O projeto utiliza JaCoCo para medir a cobertura de código. A meta é:
- **Instruções**: ≥ 80%
- **Branches**: ≥ 80%

Relatório gerado em: `target/site/jacoco/index.html`

### Estrutura de Testes

Os testes seguem a mesma estrutura do código principal:

```
src/test/java/com/fiap/comparecimento/
├── application/usecase/          # Testes de casos de uso
├── domain/                       # Testes de domínio
├── entrypoint/                  # Testes de controllers e listeners
└── infrastructure/              # Testes de infraestrutura
```

### Testcontainers

Para testes de integração com banco de dados, o projeto utiliza Testcontainers para criar containers MySQL isolados.

## 🚢 Deploy

### Build para Produção

```bash
mvn clean package -DskipTests
```

### Variáveis de Ambiente para Produção

```bash
# Database (CloudSQL)
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha_segura

# RabbitMQ
RABBIT_HOST=seu_rabbitmq_host
RABBIT_PORT=5672
RABBIT_USERNAME=seu_usuario
RABBIT_PASSWORD=sua_senha

# Server
PORT=8080
```

### Deploy Local

#### Execução Direta

```bash
# Executar JAR
java -jar target/ms-comparecimento-1.0.0-SNAPSHOT.jar

# Com variáveis de ambiente
DB_USERNAME=root \
DB_PASSWORD=senha \
RABBIT_HOST=localhost \
java -jar target/ms-comparecimento-1.0.0-SNAPSHOT.jar
```

#### Execução como Serviço (Linux)

Criar arquivo `/etc/systemd/system/ms-comparecimento.service`:

```ini
[Unit]
Description=MS Comparecimento Service
After=network.target mysql.service rabbitmq-server.service

[Service]
Type=simple
User=appuser
WorkingDirectory=/opt/ms-comparecimento
ExecStart=/usr/bin/java -jar /opt/ms-comparecimento/app.jar
Environment="DB_USERNAME=root"
Environment="DB_PASSWORD=senha"
Environment="RABBIT_HOST=localhost"
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Ativar serviço:

```bash
sudo systemctl daemon-reload
sudo systemctl enable ms-comparecimento
sudo systemctl start ms-comparecimento
sudo systemctl status ms-comparecimento
```

### Deploy em Cloud

#### Google Cloud Platform (GCP) - Cloud Run

```bash
# Build da imagem Docker
gcloud builds submit --tag gcr.io/PROJECT_ID/ms-comparecimento

# Deploy no Cloud Run
gcloud run deploy ms-comparecimento \
  --image gcr.io/PROJECT_ID/ms-comparecimento \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --set-env-vars DB_USERNAME=root \
  --set-env-vars RABBIT_HOST=rabbitmq-host \
  --set-env-vars RABBIT_PORT=5672 \
  --add-cloudsql-instances PROJECT_ID:REGION:INSTANCE_NAME \
  --set-secrets DB_PASSWORD=db-password:latest \
  --set-secrets RABBIT_PASSWORD=rabbit-password:latest
```

#### AWS - Elastic Beanstalk

```bash
# Criar aplicação
eb init ms-comparecimento --platform java

# Criar ambiente
eb create ms-comparecimento-prod

# Configurar variáveis de ambiente
eb setenv DB_USERNAME=root \
  RABBIT_HOST=rabbitmq-host \
  RABBIT_PORT=5672

# Deploy
eb deploy
```

#### Azure - App Service

```bash
# Criar app service
az webapp create \
  --resource-group myResourceGroup \
  --plan myAppServicePlan \
  --name ms-comparecimento \
  --runtime "JAVA:21-java21"

# Configurar variáveis
az webapp config appsettings set \
  --resource-group myResourceGroup \
  --name ms-comparecimento \
  --settings \
    DB_USERNAME=root \
    RABBIT_HOST=rabbitmq-host \
    RABBIT_PORT=5672

# Deploy
mvn azure-webapp:deploy
```

### Deploy com Docker

#### Docker Compose

Criar `docker-compose.yml`:

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: feedback
    ports:
      - "3306:3306"
    volumes:
      - ./ddl/create_database.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "15672:15672"
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest
    healthcheck:
      test: ["CMD", "rabbitmq-diagnostics", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  ms-comparecimento:
    build: .
    ports:
      - "8080:8080"
    environment:
      DB_USERNAME: root
      DB_PASSWORD: root
      DB_HOST: mysql
      RABBIT_HOST: rabbitmq
      RABBIT_PORT: 5672
      RABBIT_USERNAME: guest
      RABBIT_PASSWORD: guest
    depends_on:
      mysql:
        condition: service_healthy
      rabbitmq:
        condition: service_healthy
    restart: unless-stopped
```

Executar:

```bash
docker-compose up -d
```

#### Kubernetes

Criar `k8s/deployment.yaml`:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ms-comparecimento
spec:
  replicas: 3
  selector:
    matchLabels:
      app: ms-comparecimento
  template:
    metadata:
      labels:
        app: ms-comparecimento
    spec:
      containers:
      - name: ms-comparecimento
        image: ms-comparecimento:latest
        ports:
        - containerPort: 8080
        env:
        - name: DB_USERNAME
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: username
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: password
        - name: RABBIT_HOST
          value: "rabbitmq-service"
        - name: RABBIT_PORT
          value: "5672"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: ms-comparecimento-service
spec:
  selector:
    app: ms-comparecimento
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer
```

Aplicar:

```bash
kubectl apply -f k8s/deployment.yaml
kubectl get pods
kubectl get services
```

### Verificação Pós-Deploy

#### Checklist

- [ ] Aplicação inicia sem erros
- [ ] Health check retorna `UP`
- [ ] Conexão com banco de dados funcionando
- [ ] Conexão com RabbitMQ funcionando
- [ ] Endpoints REST respondem corretamente
- [ ] Swagger UI acessível
- [ ] Logs sendo gerados corretamente
- [ ] Mensagens sendo consumidas da fila
- [ ] Cálculo de ICC funcionando

#### Testes de Integração

```bash
# Testar endpoint de pacientes
curl "http://localhost:8080/v1/pacientes/indice-comparecimento?cns=123456789012345"

# Testar endpoint de relatórios
curl "http://localhost:8080/v1/relatorios/absenteismo?dataInicio=2026-01-01&dataFim=2026-01-31"

# Enviar mensagem de teste para RabbitMQ
rabbitmqadmin publish exchange=agendamento.exchange \
  routing_key=agendamento.key \
  payload='{"cns":"123456789012345","statusConsulta":"REALIZADO","statusNotificacao":"CONFIRMOU_24H","dataEvento":"2026-01-15T09:30:00-03:00"}'
```

## 🏛️ Arquitetura Detalhada

### Visão Geral da Arquitetura

O projeto segue os princípios de **Clean Architecture** e **Hexagonal Architecture (Ports & Adapters)**, garantindo separação de responsabilidades e testabilidade.

### Camadas da Aplicação

#### 1. Domain Layer (Camada de Domínio)

**Responsabilidade**: Contém as regras de negócio puras, independentes de frameworks e tecnologias.

**Componentes:**
- **Enums**: `ClassificacaoPacienteEnum`, `StatusConsultaEnum`, `StatusNotificacaoEnum`
- **Models**: `PacienteDomain`, `EventoAgendamentoMessageDomain`, `RelatorioAbsenteismoDomain`, `PeriodoDomain`
- **Exceptions**: Exceções de domínio customizadas

**Características:**
- Não depende de nenhuma outra camada
- Contém apenas lógica de negócio
- Pode ser testada isoladamente

#### 2. Application Layer (Camada de Aplicação)

**Responsabilidade**: Orquestra os casos de uso da aplicação e define contratos de entrada/saída.

**Componentes:**
- **Gateways (Ports)**: Interfaces que definem contratos para acesso a dados externos
  - `PacienteGateway`: Interface para operações de pacientes
  
- **Use Cases**: Implementam a lógica de casos de uso
  - `CalculaComparecimentoUseCase`: Calcula e atualiza o ICC do paciente
  - `ConsultarIndiceComparecimentoPacienteUseCase`: Consulta índice de um paciente
  - `ConsultarIndicadoresPorPeriodoUseCase`: Gera relatórios por período

**Características:**
- Depende apenas da camada de domínio
- Define interfaces (ports) para infraestrutura
- Contém regras de aplicação e orquestração

#### 3. Infrastructure Layer (Camada de Infraestrutura)

**Responsabilidade**: Implementa adaptadores para tecnologias externas.

**Componentes:**
- **Database**: Implementações de persistência
  - `PacienteGatewayImpl`: Implementação do gateway usando JPA
  - `PacienteRepository`: Repositório Spring Data JPA
  - `PacienteEntity`: Entidade JPA
  - `PacienteEntityMapper`: Mapeamento Entity ↔ Domain

- **Config**: Configurações de infraestrutura
  - `RabbitMQConfig`: Configuração de filas, exchanges e bindings
  - Configurações de casos de uso (Beans)

**Características:**
- Implementa interfaces definidas na camada de aplicação
- Pode ser substituída sem afetar outras camadas
- Contém detalhes de implementação técnica

#### 4. Entrypoint Layer (Camada de Entrada)

**Responsabilidade**: Pontos de entrada da aplicação (HTTP, Mensageria).

**Componentes:**
- **Controllers**: REST Controllers
  - `PacientesController`: Endpoints de pacientes
  - `RelatoriosController`: Endpoints de relatórios
  - Mappers: Conversão DTO ↔ Domain
  - Presenters: Formatação de respostas

- **Listeners**: Message Listeners (RabbitMQ)
  - `ComparecimentoConsumer`: Consome eventos de agendamento
  - Mappers: Conversão Message ↔ Domain

**Características:**
- Depende da camada de aplicação
- Lida com protocolos de comunicação
- Converte entre formatos externos e internos

### Fluxo de Dados Detalhado

#### Fluxo de Processamento de Eventos

```
RabbitMQ Queue
    ↓
ComparecimentoConsumer (Entrypoint)
    ↓
EventoAgendamentoMessageDto → EventoAgendamentoMessageDomain (Mapper)
    ↓
CalculaComparecimentoUseCase (Application)
    ↓
PacienteGateway.consultar(cns) (Application Port)
    ↓
PacienteGatewayImpl.consultar(cns) (Infrastructure)
    ↓
PacienteRepository.findByCns(cns) (Infrastructure)
    ↓
PacienteEntity → PacienteDomain (Mapper)
    ↓
Cálculo do ICC (Application Use Case)
    ↓
PacienteGateway.atualizarInformacoesPaciente(domain) (Application Port)
    ↓
PacienteGatewayImpl.atualizarInformacoesPaciente(domain) (Infrastructure)
    ↓
PacienteRepository.save(entity) (Infrastructure)
    ↓
MySQL Database
```

#### Fluxo de Consulta REST

```
HTTP GET /v1/pacientes/indice-comparecimento?cns=...
    ↓
PacientesController (Entrypoint)
    ↓
ConsultarIndiceComparecimentoPacienteUseCase (Application)
    ↓
PacienteGateway.consultar(cns) (Application Port)
    ↓
PacienteGatewayImpl.consultar(cns) (Infrastructure)
    ↓
PacienteRepository.findByCns(cns) (Infrastructure)
    ↓
PacienteEntity → PacienteDomain (Mapper)
    ↓
PacienteDomain → IndiceComparecimentoResponseDto (Presenter)
    ↓
HTTP 200 OK + JSON Response
```

### Padrões de Design Utilizados

#### 1. Dependency Inversion Principle (DIP)

As camadas superiores não dependem das inferiores. Interfaces são definidas nas camadas superiores e implementadas nas inferiores.

**Exemplo**:
```java
// Application Layer define a interface
public interface PacienteGateway {
    PacienteDomain consultar(String cns);
}

// Infrastructure Layer implementa
public class PacienteGatewayImpl implements PacienteGateway {
    // implementação
}
```

#### 2. Repository Pattern

Abstração do acesso a dados através de interfaces.

**Exemplo**:
```java
public interface PacienteRepository extends JpaRepository<PacienteEntity, String> {
    PacienteEntity findByCns(String cns);
}
```

#### 3. Use Case Pattern

Cada caso de uso é uma classe isolada com responsabilidade única.

**Exemplo**:
```java
public interface CalculaComparecimentoUseCase {
    void calculaComparecimento(EventoAgendamentoMessageDomain evento);
}
```

#### 4. Mapper Pattern

Conversão entre diferentes representações de dados (DTO, Entity, Domain).

**Exemplo**:
```java
@Mapper(componentModel = "spring")
public interface PacienteEntityMapper {
    PacienteDomain toDomain(PacienteEntity entity);
    PacienteEntity toEntity(PacienteDomain domain);
}
```

#### 5. Presenter Pattern

Formatação de dados para apresentação (Domain → DTO).

**Exemplo**:
```java
public class IndiceComparecimentoPacientePresenter {
    public static IndiceComparecimentoResponseDto toDto(PacienteDomain domain) {
        // conversão
    }
}
```

### Configuração de Dependências

#### Injeção de Dependências

O Spring gerencia todas as dependências através de injeção por construtor:

```java
public class CalculaComparecimentoUseCaseImpl implements CalculaComparecimentoUseCase {
    private final PacienteGateway pacienteGateway;
    
    public CalculaComparecimentoUseCaseImpl(PacienteGateway pacienteGateway) {
        this.pacienteGateway = pacienteGateway;
    }
}
```

#### Configuração de Beans

Beans são configurados através de classes `@Configuration`:

```java
@Configuration
public class CalculaComparecimentoConfig {
    @Bean
    public CalculaComparecimentoUseCase calculaComparecimentoUseCase(
            PacienteGateway pacienteGateway) {
        return new CalculaComparecimentoUseCaseImpl(pacienteGateway);
    }
}
```

### Performance e Escalabilidade

#### Connection Pooling

- **HikariCP**: Pool de conexões configurado
  - `maximum-pool-size`: 5
  - `minimum-idle`: 0
  - `connection-timeout`: 30000ms

#### Mensageria

- **Concurrency**: 1-5 workers
- **Prefetch**: 10 mensagens por worker
- **Retry**: 3 tentativas com backoff exponencial

#### Banco de Dados

- **Índices**: Criados em campos frequentemente consultados
- **Queries**: Otimizadas através de projections quando necessário

### Decisões Arquiteturais

#### Por que Clean Architecture?

- **Testabilidade**: Facilita testes isolados
- **Manutenibilidade**: Código organizado e fácil de entender
- **Flexibilidade**: Permite trocar tecnologias sem afetar regras de negócio

#### Por que Domain-Driven Design?

- **Modelagem**: Domínio rico e expressivo
- **Ubiquidade**: Linguagem comum entre desenvolvedores e negócio
- **Foco**: Concentração nas regras de negócio

#### Por que Mensageria Assíncrona?

- **Desacoplamento**: Produtores e consumidores independentes
- **Escalabilidade**: Processamento paralelo
- **Resiliência**: Retry automático e filas duráveis

## 📝 Contribuindo

### Como Contribuir

1. **Fork** o repositório
2. **Clone** seu fork:
   ```bash
   git clone https://github.com/seu-usuario/ms-comparecimento.git
   cd ms-comparecimento
   ```
3. Crie uma **branch** para sua feature:
   ```bash
   git checkout -b feature/minha-feature
   ```
4. Faça suas **alterações**
5. **Teste** suas alterações
6. **Commit** suas mudanças (veja [Commits](#commits))
7. **Push** para sua branch:
   ```bash
   git push origin feature/minha-feature
   ```
8. Abra um **Pull Request**

### Padrões de Código

#### Convenções Java

- Seguir as convenções de nomenclatura Java:
  - Classes: `PascalCase`
  - Métodos e variáveis: `camelCase`
  - Constantes: `UPPER_SNAKE_CASE`
  - Pacotes: `lowercase`

#### Estrutura de Arquivos

Manter a estrutura de pacotes seguindo Clean Architecture:

```
com.fiap.comparecimento
├── domain/          # Regras de negócio puras
├── application/     # Casos de uso e interfaces
├── infrastructure/  # Implementações técnicas
└── entrypoint/      # Pontos de entrada (HTTP, Messaging)
```

#### Formatação

- Usar 4 espaços para indentação (não tabs)
- Linhas com no máximo 120 caracteres
- Remover imports não utilizados
- Organizar imports (IDE geralmente faz isso automaticamente)

#### Documentação

- Documentar métodos públicos com JavaDoc
- Adicionar comentários explicativos para lógica complexa
- Manter README e documentação atualizados

### Testes

#### Tipos de Testes

1. **Testes Unitários**: Testam unidades isoladas (classes, métodos)
2. **Testes de Integração**: Testam integração entre componentes
3. **Testes de Contrato**: Testam APIs REST

#### Nomenclatura

- Classes de teste: `NomeDaClasseTest`
- Métodos de teste: `deveFazerAlgoQuandoCondicao()`

#### Exemplo

```java
@ExtendWith(MockitoExtension.class)
class CalculaComparecimentoUseCaseImplTest {
    
    @Mock
    private PacienteGateway pacienteGateway;
    
    @InjectMocks
    private CalculaComparecimentoUseCaseImpl useCase;
    
    @Test
    void deveCalcularICCQuandoPacienteExiste() {
        // Given
        PacienteDomain paciente = criarPacienteMock();
        EventoAgendamentoMessageDomain evento = criarEventoMock();
        
        // When
        useCase.calculaComparecimento(evento);
        
        // Then
        verify(pacienteGateway).atualizarInformacoesPaciente(any());
    }
}
```

### Commits

#### Mensagens de Commit

Seguir o padrão [Conventional Commits](https://www.conventionalcommits.org/):

```
<tipo>(<escopo>): <descrição>

[corpo opcional]

[rodapé opcional]
```

#### Tipos

- `feat`: Nova funcionalidade
- `fix`: Correção de bug
- `docs`: Documentação
- `style`: Formatação (não afeta código)
- `refactor`: Refatoração
- `test`: Testes
- `chore`: Tarefas de manutenção

#### Exemplos

```
feat(usecase): adiciona cálculo de ICC para novos pacientes

fix(controller): corrige tratamento de CNS inválido

docs(readme): atualiza instruções de instalação

refactor(domain): simplifica lógica de classificação
```

### Pull Requests

#### Antes de Abrir um PR

- [ ] Código compila sem erros
- [ ] Todos os testes passam
- [ ] Cobertura de testes ≥ 80%
- [ ] Documentação atualizada (se necessário)
- [ ] Código segue os padrões estabelecidos
- [ ] Não há conflitos com a branch principal

#### Template de PR

```markdown
## Descrição
Breve descrição das mudanças realizadas.

## Tipo de Mudança
- [ ] Bug fix
- [ ] Nova funcionalidade
- [ ] Breaking change
- [ ] Documentação

## Como Testar
Passos para testar as mudanças:
1. ...
2. ...

## Checklist
- [ ] Testes adicionados/atualizados
- [ ] Documentação atualizada
- [ ] Código segue padrões do projeto
- [ ] Sem warnings do compilador
```

## 🔍 Troubleshooting

### Problemas Comuns

#### Aplicação não inicia

```bash
# Verificar logs
tail -f /var/log/ms-comparecimento/application.log

# Verificar variáveis de ambiente
env | grep -E "DB_|RABBIT_"

# Verificar conectividade
nc -zv localhost 3306  # MySQL
nc -zv localhost 5672  # RabbitMQ
```

#### Erro de conexão com banco

```bash
# Verificar credenciais
mysql -u root -p -h localhost -e "SELECT 1"

# Verificar se banco existe
mysql -u root -p -e "SHOW DATABASES LIKE 'feedback';"

# Verificar tabelas
mysql -u root -p -e "USE feedback; SHOW TABLES;"
```

#### Erro de conexão com RabbitMQ

```bash
# Verificar se RabbitMQ está rodando
rabbitmqctl status

# Verificar filas
rabbitmqctl list_queues

# Verificar exchanges
rabbitmqctl list_exchanges
```

#### Porta já em uso

```bash
# Verificar processo na porta 8080
lsof -i :8080

# Matar processo
kill -9 PID

# Ou mudar porta
export PORT=8081
```

### Logs

```bash
# Logs da aplicação
tail -f logs/application.log

# Logs do Docker
docker logs -f ms-comparecimento

# Logs do Kubernetes
kubectl logs -f deployment/ms-comparecimento
```

### Performance

```bash
# Verificar uso de memória
jmap -heap <PID>

# Verificar threads
jstack <PID>

# Profiling
jstat -gc <PID> 1000
```

## 📄 Licença

Este projeto foi desenvolvido para o Hackathon da FIAP - Módulo 5.

## 👥 Autores

- Equipe FIAP Hackathon

## 📞 Suporte

Para dúvidas ou problemas, abra uma issue no repositório.

---

**Versão**: 1.0.0-SNAPSHOT  
**Última atualização**: Fevereiro 2026
