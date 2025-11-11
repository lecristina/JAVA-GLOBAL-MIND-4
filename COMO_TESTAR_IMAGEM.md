# 📸 Como Testar Upload de Imagem - Visão Computacional

Guia passo a passo para testar o endpoint de análise de ambiente de trabalho usando Deep Learning.

---

## 🎯 Endpoint

**POST** `/ia/analise-ambiente`

**Autenticação:** Bearer Token (JWT)

**Content-Type:** `multipart/form-data`

---

## 📋 Pré-requisitos

1. ✅ API rodando em `http://localhost:8080`
2. ✅ Token JWT válido (obtido via `/api/auth/login`)
3. ✅ Uma imagem de ambiente de trabalho (JPEG, PNG, etc)

---

## 🚀 Método 1: Swagger UI (MAIS FÁCIL)

### Passo a Passo:

1. **Inicie a aplicação:**
   ```bash
   mvn spring-boot:run
   ```

2. **Acesse o Swagger:**
   ```
   http://localhost:8080/swagger-ui.html
   ```

3. **Faça Login:**
   - Vá em `POST /api/auth/login`
   - Clique em "Try it out"
   - Preencha:
     ```json
     {
       "email": "seu-email@example.com",
       "senha": "sua-senha"
     }
     ```
   - Clique em "Execute"
   - **COPIE O TOKEN** da resposta

4. **Autorize no Swagger:**
   - Clique no botão **"Authorize"** (cadeado verde no topo)
   - Cole o token (SEM a palavra "Bearer")
   - Clique em "Authorize" e depois "Close"

5. **Teste o Endpoint de Imagem:**
   - Procure por `POST /ia/analise-ambiente`
   - Clique em "Try it out"
   - Preencha:
     - `usuarioId`: `1` (ou o ID do seu usuário)
     - `foto`: Clique em "Choose File" e selecione uma imagem
   - Clique em "Execute"

6. **Veja o Resultado:**
   - A resposta mostrará:
     - `nivelFoco`: alto/médio/baixo
     - `organizacao`: excelente/boa/regular/ruim
     - `iluminacao`: excelente/adequada/insuficiente
     - `objetosDetectados`: lista de objetos com porcentagem
     - `sugestoes`: dicas práticas
     - `idAlerta`: ID salvo no banco

---

## 🖥️ Método 2: Postman

### Passo a Passo:

1. **Crie uma Nova Request:**
   - Método: **POST**
   - URL: `http://localhost:8080/ia/analise-ambiente`

2. **Configure Headers:**
   - Vá na aba "Headers"
   - Adicione:
     ```
     Key: Authorization
     Value: Bearer {seu-token-aqui}
     ```
   - ⚠️ **NÃO** adicione Content-Type manualmente (Postman faz isso automaticamente para multipart)

3. **Configure Body:**
   - Vá na aba "Body"
   - Selecione **"form-data"**
   - Adicione dois campos:

   **Campo 1:**
   - Key: `foto`
   - Tipo: Selecione **"File"** (não Text!)
   - Value: Clique em "Select Files" e escolha sua imagem

   **Campo 2:**
   - Key: `usuarioId`
   - Tipo: **"Text"**
   - Value: `1`

4. **Envie a Request:**
   - Clique em "Send"
   - Veja a resposta JSON com a análise

### Exemplo Visual no Postman:

```
┌─────────────────────────────────────┐
│ POST http://localhost:8080/ia/...  │
├─────────────────────────────────────┤
│ Headers:                            │
│ Authorization: Bearer eyJhbGc...   │
├─────────────────────────────────────┤
│ Body: form-data                     │
│ ┌──────────┬──────┬──────────────┐ │
│ │ Key      │ Type │ Value        │ │
│ ├──────────┼──────┼──────────────┤ │
│ │ foto     │ File │ [Choose File]│ │
│ │ usuarioId│ Text │ 1            │ │
│ └──────────┴──────┴──────────────┘ │
└─────────────────────────────────────┘
```

---

## 💻 Método 3: cURL (Linha de Comando)

### Windows (PowerShell):

```powershell
# 1. Primeiro, obtenha o token (substitua email e senha)
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
    -Method POST `
    -ContentType "application/json" `
    -Body '{"email":"seu-email@example.com","senha":"sua-senha"}'

$token = $loginResponse.token

# 2. Envie a imagem
curl.exe -X POST "http://localhost:8080/ia/analise-ambiente" `
    -H "Authorization: Bearer $token" `
    -F "foto=@C:\Users\crist\Downloads\ambiente-trabalho.jpg" `
    -F "usuarioId=1"
```

### Windows (CMD):

```cmd
curl -X POST "http://localhost:8080/ia/analise-ambiente" ^
    -H "Authorization: Bearer SEU_TOKEN_AQUI" ^
    -F "foto=@C:\Users\crist\Downloads\ambiente-trabalho.jpg" ^
    -F "usuarioId=1"
```

### Linux/Mac:

```bash
# 1. Obtenha o token
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"seu-email@example.com","senha":"sua-senha"}' \
  | jq -r '.token')

# 2. Envie a imagem
curl -X POST http://localhost:8080/ia/analise-ambiente \
  -H "Authorization: Bearer $TOKEN" \
  -F "foto=@/home/usuario/ambiente-trabalho.jpg" \
  -F "usuarioId=1"
```

---

## 🌐 Método 4: JavaScript (Frontend/Node.js)

### HTML + JavaScript:

```html
<!DOCTYPE html>
<html>
<head>
    <title>Teste Upload Imagem</title>
</head>
<body>
    <h1>Teste de Análise de Ambiente</h1>
    
    <input type="file" id="fileInput" accept="image/*">
    <input type="number" id="usuarioId" value="1" placeholder="ID do Usuário">
    <button onclick="analisarAmbiente()">Analisar Ambiente</button>
    
    <div id="resultado"></div>

    <script>
        const TOKEN = "SEU_TOKEN_JWT_AQUI"; // Cole seu token aqui

        async function analisarAmbiente() {
            const fileInput = document.getElementById('fileInput');
            const usuarioId = document.getElementById('usuarioId').value;
            const resultadoDiv = document.getElementById('resultado');

            if (!fileInput.files[0]) {
                alert('Selecione uma imagem!');
                return;
            }

            const formData = new FormData();
            formData.append('foto', fileInput.files[0]);
            formData.append('usuarioId', usuarioId);

            try {
                resultadoDiv.innerHTML = '⏳ Analisando...';

                const response = await fetch('http://localhost:8080/ia/analise-ambiente', {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${TOKEN}`
                        // NÃO adicione Content-Type! O browser faz isso automaticamente
                    },
                    body: formData
                });

                if (!response.ok) {
                    throw new Error(`Erro: ${response.status} - ${response.statusText}`);
                }

                const data = await response.json();
                
                // Exibir resultado
                resultadoDiv.innerHTML = `
                    <h2>✅ Análise Completa</h2>
                    <p><strong>Nível de Foco:</strong> ${data.nivelFoco}</p>
                    <p><strong>Organização:</strong> ${data.organizacao}</p>
                    <p><strong>Iluminação:</strong> ${data.iluminacao}</p>
                    <p><strong>Objetos Detectados:</strong></p>
                    <ul>
                        ${data.objetosDetectados.map(obj => `<li>${obj}</li>`).join('')}
                    </ul>
                    <p><strong>Sugestões:</strong></p>
                    <ul>
                        ${data.sugestoes.map(sug => `<li>${sug}</li>`).join('')}
                    </ul>
                    <p><strong>Resumo:</strong> ${data.resumoAnalise}</p>
                    <p><strong>ID Alerta:</strong> ${data.idAlerta}</p>
                `;
            } catch (error) {
                resultadoDiv.innerHTML = `<p style="color: red;">❌ Erro: ${error.message}</p>`;
            }
        }
    </script>
</body>
</html>
```

### Node.js (com axios):

```javascript
const axios = require('axios');
const FormData = require('form-data');
const fs = require('fs');

async function testarAnaliseAmbiente() {
    const token = 'SEU_TOKEN_JWT_AQUI';
    const caminhoImagem = './ambiente-trabalho.jpg';
    const usuarioId = 1;

    const formData = new FormData();
    formData.append('foto', fs.createReadStream(caminhoImagem));
    formData.append('usuarioId', usuarioId.toString());

    try {
        const response = await axios.post(
            'http://localhost:8080/ia/analise-ambiente',
            formData,
            {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    ...formData.getHeaders()
                }
            }
        );

        console.log('✅ Análise Completa:');
        console.log('Nível de Foco:', response.data.nivelFoco);
        console.log('Organização:', response.data.organizacao);
        console.log('Iluminação:', response.data.iluminacao);
        console.log('Objetos Detectados:', response.data.objetosDetectados);
        console.log('Sugestões:', response.data.sugestoes);
        console.log('ID Alerta:', response.data.idAlerta);
    } catch (error) {
        console.error('❌ Erro:', error.response?.data || error.message);
    }
}

testarAnaliseAmbiente();
```

---

## 🐍 Método 5: Python (requests)

```python
import requests

# Configurações
url = "http://localhost:8080/ia/analise-ambiente"
token = "SEU_TOKEN_JWT_AQUI"
usuario_id = 1
caminho_imagem = "ambiente-trabalho.jpg"

# Preparar requisição
headers = {
    "Authorization": f"Bearer {token}"
}

files = {
    "foto": open(caminho_imagem, "rb")
}

data = {
    "usuarioId": usuario_id
}

try:
    print("⏳ Enviando imagem para análise...")
    response = requests.post(url, headers=headers, files=files, data=data)
    
    if response.status_code == 200:
        resultado = response.json()
        print("\n✅ Análise Completa:")
        print(f"Nível de Foco: {resultado['nivelFoco']}")
        print(f"Organização: {resultado['organizacao']}")
        print(f"Iluminação: {resultado['iluminacao']}")
        print(f"\nObjetos Detectados:")
        for obj in resultado['objetosDetectados']:
            print(f"  - {obj}")
        print(f"\nSugestões:")
        for sug in resultado['sugestoes']:
            print(f"  - {sug}")
        print(f"\nResumo: {resultado['resumoAnalise']}")
        print(f"ID Alerta: {resultado['idAlerta']}")
    else:
        print(f"❌ Erro: {response.status_code}")
        print(response.text)
        
except Exception as e:
    print(f"❌ Erro: {e}")
finally:
    files['foto'].close()
```

---

## 📱 Método 6: Insomnia

1. **Crie uma Nova Request:**
   - Método: POST
   - URL: `http://localhost:8080/ia/analise-ambiente`

2. **Headers:**
   - `Authorization`: `Bearer {seu-token}`

3. **Body:**
   - Selecione "Multipart Form"
   - Adicione:
     - `foto`: Tipo "File", selecione sua imagem
     - `usuarioId`: Tipo "Text", valor `1`

4. **Send**

---

## ✅ Resposta Esperada

```json
{
  "nivelFoco": "alto",
  "organizacao": "boa",
  "iluminacao": "excelente",
  "objetosDetectados": [
    "desk (95.23%)",
    "computer (87.45%)",
    "monitor (82.10%)"
  ],
  "sugestoes": [
    "Mantenha o ambiente organizado para melhorar a produtividade",
    "Faça pausas regulares para descansar os olhos"
  ],
  "resumoAnalise": "✅ Análise realizada com modelo de Deep Learning (IA REAL). Detectados 3 elementos no ambiente usando visão computacional. Nível de foco: alto. Organização: boa. Iluminação: excelente.",
  "timestamp": "2024-11-11T15:30:00",
  "idAlerta": 124
}
```

---

## 🔍 Verificando se Usa IA Real

### Nos Logs da Aplicação:

Procure por estas mensagens:

**✅ IA REAL (funcionando):**
```
✅ IA REAL: Análise recebida do modelo de Deep Learning. Resultado: [...]
✅ Processando resultados REAIS do modelo de IA (3 itens detectados)
```

**⚠️ FALLBACK (sem IA):**
```
⚠️ FALLBACK: Usando análise heurística (API não retornou resultados válidos)
⚠️ Modelo Hugging Face ainda carregando (503). Usando análise heurística como fallback.
```

### Na Resposta JSON:

- **Com IA Real:** `resumoAnalise` contém "✅ Análise realizada com modelo de Deep Learning (IA REAL)"
- **Com Fallback:** `resumoAnalise` contém "⚠️ Análise baseada em padrões comuns (fallback - IA não disponível)"

---

## 🐛 Troubleshooting

### Erro 401 (Unauthorized):
- ✅ Verifique se o token está correto
- ✅ Faça login novamente para obter novo token
- ✅ Certifique-se de colar o token SEM "Bearer" no Swagger

### Erro 400 (Bad Request):
- ✅ Verifique se o arquivo é uma imagem válida (JPEG, PNG)
- ✅ Verifique se `usuarioId` é um número válido
- ✅ Certifique-se de usar `multipart/form-data` (não JSON)

### Erro 500 (Internal Server Error):
- ✅ Verifique os logs da aplicação
- ✅ Verifique se a imagem não está corrompida
- ✅ Tente com uma imagem menor (< 10MB)

### Resposta com Fallback:
- ⚠️ A API Hugging Face pode estar carregando (primeira chamada)
- ⚠️ Aguarde alguns segundos e tente novamente
- ⚠️ Verifique conexão com internet

---

## 📝 Dicas

1. **Use imagens reais de ambiente de trabalho** para melhores resultados
2. **Primeira chamada pode demorar** (modelo carregando na Hugging Face)
3. **Verifique os logs** para ver se está usando IA real ou fallback
4. **Teste com diferentes imagens** para ver variações na análise

---

## 🎬 Exemplo Completo (cURL + jq)

```bash
# 1. Login e obter token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"teste@example.com","senha":"senha123"}' \
  | jq -r '.token')

echo "Token obtido: ${TOKEN:0:20}..."

# 2. Analisar imagem
curl -X POST http://localhost:8080/ia/analise-ambiente \
  -H "Authorization: Bearer $TOKEN" \
  -F "foto=@ambiente.jpg" \
  -F "usuarioId=1" \
  | jq '.'

# 3. Verificar se usou IA real
curl -X POST http://localhost:8080/ia/analise-ambiente \
  -H "Authorization: Bearer $TOKEN" \
  -F "foto=@ambiente.jpg" \
  -F "usuarioId=1" \
  | jq -r '.resumoAnalise' | grep -q "IA REAL" && echo "✅ Usando IA Real!" || echo "⚠️ Usando Fallback"
```

---

**Última atualização:** 11/11/2024

