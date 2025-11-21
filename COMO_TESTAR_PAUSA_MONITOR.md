# 🧪 Como Testar o Monitoramento de Pausas

Guia completo para testar o endpoint `/ia/pausa-monitor`.

## 📋 Pré-requisitos

1. **Aplicação rodando**: `mvn spring-boot:run`
2. **Token JWT válido**: Faça login primeiro em `/api/auth/login`
3. **Imagem para teste**: Pode usar qualquer imagem (JPEG/PNG)

---

## 🚀 Método 1: Teste com cURL (Mais Simples)

### Passo 1: Fazer Login e Obter Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "seu-email@example.com",
    "senha": "sua-senha"
  }'
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipoToken": "Bearer",
  "usuario": { ... }
}
```

### Passo 2: Converter Imagem para Base64

**No Windows (PowerShell):**
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("C:\caminho\para\imagem.jpg"))
```

**No Linux/Mac:**
```bash
base64 -i imagem.jpg
```

**Ou use um site online:**
- https://base64.guru/converter/encode/image

### Passo 3: Enviar Frame para Monitoramento

```bash
TOKEN="seu-token-jwt-aqui"
FRAME_BASE64="iVBORw0KGgoAAAANS..." # Cole o base64 da imagem aqui

curl -X POST http://localhost:8080/ia/pausa-monitor \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"usuarioId\": 1,
    \"frameBase64\": \"$FRAME_BASE64\"
  }"
```

**Resposta esperada:**
```json
{
  "usuarioId": 1,
  "movimentoDetectado": true,
  "quantidadeMovimento": 25000,
  "presente": true,
  "tempoSentadoMinutos": 0,
  "totalPausas": 0,
  "sugerirAlongamento": false,
  "mensagem": "Movimento detectado (25000 pixels diferentes). Usuário presente.",
  "sugestoes": ["✅ Continue monitorando. Lembre-se de fazer pausas a cada 90 minutos"],
  "timestamp": "2025-01-15T10:30:00"
}
```

### Passo 4: Enviar Múltiplos Frames (Simular Sessão)

```bash
# Frame 1 (primeiro frame - sempre detecta movimento)
curl -X POST http://localhost:8080/ia/pausa-monitor \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"usuarioId\": 1,
    \"frameBase64\": \"$FRAME_BASE64_1\"
  }"

# Aguardar 5 segundos
sleep 5

# Frame 2 (mesma imagem = sem movimento)
curl -X POST http://localhost:8080/ia/pausa-monitor \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"usuarioId\": 1,
    \"frameBase64\": \"$FRAME_BASE64_1\"
  }"

# Frame 3 (imagem diferente = movimento detectado)
curl -X POST http://localhost:8080/ia/pausa-monitor \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"usuarioId\": 1,
    \"frameBase64\": \"$FRAME_BASE64_2\"
  }"
```

---

## 🌐 Método 2: Teste com Postman

### 1. Criar Nova Requisição

- **Método**: `POST`
- **URL**: `http://localhost:8080/ia/pausa-monitor`

### 2. Configurar Headers

```
Content-Type: application/json
Authorization: Bearer seu-token-jwt-aqui
```

### 3. Configurar Body (JSON)

```json
{
  "usuarioId": 1,
  "frameBase64": "iVBORw0KGgoAAAANS...",
  "resetarSessao": false
}
```

### 4. Converter Imagem para Base64 no Postman

1. Vá em **Body** → **raw** → **JSON**
2. Use um conversor online: https://base64.guru/converter/encode/image
3. Cole o resultado no campo `frameBase64`

---

## 💻 Método 3: Teste com JavaScript (Webcam)

Crie um arquivo HTML para testar com webcam real:

```html
<!DOCTYPE html>
<html>
<head>
    <title>Teste Monitoramento de Pausas</title>
</head>
<body>
    <h1>Monitoramento de Pausas - Teste com Webcam</h1>
    <video id="webcam" width="640" height="480" autoplay></video>
    <br><br>
    <button onclick="iniciarMonitoramento()">Iniciar Monitoramento</button>
    <button onclick="pararMonitoramento()">Parar</button>
    <button onclick="resetarSessao()">Resetar Sessão</button>
    <br><br>
    <div id="status"></div>
    <div id="resultado"></div>

    <script>
        const video = document.getElementById('webcam');
        const statusDiv = document.getElementById('status');
        const resultadoDiv = document.getElementById('resultado');
        let intervalo = null;
        
        // Token JWT - SUBSTITUA PELO SEU TOKEN
        const TOKEN = 'seu-token-jwt-aqui';
        const USUARIO_ID = 1;
        const API_URL = 'http://localhost:8080/ia/pausa-monitor';

        // Solicitar acesso à webcam
        navigator.mediaDevices.getUserMedia({ video: true })
            .then(stream => {
                video.srcObject = stream;
                statusDiv.innerHTML = '✅ Webcam conectada';
            })
            .catch(err => {
                statusDiv.innerHTML = '❌ Erro ao acessar webcam: ' + err.message;
            });

        function capturarFrame() {
            const canvas = document.createElement('canvas');
            canvas.width = video.videoWidth;
            canvas.height = video.videoHeight;
            const ctx = canvas.getContext('2d');
            ctx.drawImage(video, 0, 0);
            
            // Converter para base64
            return canvas.toDataURL('image/jpeg', 0.8).split(',')[1];
        }

        async function enviarFrame(frameBase64) {
            try {
                const response = await fetch(API_URL, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${TOKEN}`
                    },
                    body: JSON.stringify({
                        usuarioId: USUARIO_ID,
                        frameBase64: frameBase64
                    })
                });

                if (!response.ok) {
                    throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                }

                const resultado = await response.json();
                exibirResultado(resultado);
                return resultado;
            } catch (error) {
                console.error('Erro ao enviar frame:', error);
                resultadoDiv.innerHTML = `<p style="color: red;">❌ Erro: ${error.message}</p>`;
            }
        }

        function exibirResultado(resultado) {
            let html = '<h3>📊 Resultado do Monitoramento</h3>';
            html += `<p><strong>Movimento:</strong> ${resultado.movimentoDetectado ? '✅ Detectado' : '❌ Não detectado'}</p>`;
            html += `<p><strong>Quantidade de Movimento:</strong> ${resultado.quantidadeMovimento} pixels</p>`;
            html += `<p><strong>Presente:</strong> ${resultado.presente ? '✅ Sim' : '❌ Não'}</p>`;
            html += `<p><strong>Tempo Sentado:</strong> ${resultado.tempoSentadoMinutos} minutos</p>`;
            html += `<p><strong>Total de Pausas:</strong> ${resultado.totalPausas}</p>`;
            html += `<p><strong>Mensagem:</strong> ${resultado.mensagem}</p>`;
            
            if (resultado.sugerirAlongamento) {
                html += '<div style="background: #fff3cd; padding: 10px; border-radius: 5px; margin-top: 10px;">';
                html += '<h4>💡 Sugestões de Alongamento:</h4><ul>';
                resultado.sugestoes.forEach(sugestao => {
                    html += `<li>${sugestao}</li>`;
                });
                html += '</ul></div>';
            }
            
            resultadoDiv.innerHTML = html;
        }

        function iniciarMonitoramento() {
            if (intervalo) {
                alert('Monitoramento já está ativo!');
                return;
            }

            statusDiv.innerHTML = '🔄 Monitoramento iniciado (enviando frames a cada 10 segundos)...';
            
            // Enviar primeiro frame imediatamente
            const frame1 = capturarFrame();
            enviarFrame(frame1);

            // Enviar frames a cada 10 segundos
            intervalo = setInterval(() => {
                const frame = capturarFrame();
                enviarFrame(frame);
            }, 10000); // 10 segundos
        }

        function pararMonitoramento() {
            if (intervalo) {
                clearInterval(intervalo);
                intervalo = null;
                statusDiv.innerHTML = '⏹️ Monitoramento parado';
            }
        }

        async function resetarSessao() {
            const frame = capturarFrame();
            try {
                const response = await fetch(API_URL, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${TOKEN}`
                    },
                    body: JSON.stringify({
                        usuarioId: USUARIO_ID,
                        frameBase64: frame,
                        resetarSessao: true
                    })
                });
                const resultado = await response.json();
                alert('✅ Sessão resetada com sucesso!');
                exibirResultado(resultado);
            } catch (error) {
                alert('❌ Erro ao resetar sessão: ' + error.message);
            }
        }
    </script>
</body>
</html>
```

**Como usar:**
1. Salve o código acima em um arquivo `teste-pausa-monitor.html`
2. Abra no navegador
3. Permita acesso à webcam quando solicitado
4. Clique em "Iniciar Monitoramento"
5. Observe os resultados sendo atualizados a cada 10 segundos

---

## 🐍 Método 4: Teste com Python

Script Python para capturar webcam e enviar frames:

```python
import cv2
import base64
import requests
import time
import json

# Configurações
API_URL = "http://localhost:8080/ia/pausa-monitor"
TOKEN = "seu-token-jwt-aqui"
USUARIO_ID = 1
INTERVALO_SEGUNDOS = 10  # Enviar frame a cada 10 segundos

def capturar_frame():
    """Captura um frame da webcam"""
    cap = cv2.VideoCapture(0)
    ret, frame = cap.read()
    cap.release()
    
    if not ret:
        raise Exception("Erro ao capturar frame da webcam")
    
    # Converter para JPEG
    _, buffer = cv2.imencode('.jpg', frame)
    frame_bytes = buffer.tobytes()
    
    # Converter para base64
    frame_base64 = base64.b64encode(frame_bytes).decode('utf-8')
    return frame_base64

def enviar_frame(frame_base64, resetar_sessao=False):
    """Envia frame para a API"""
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {TOKEN}"
    }
    
    payload = {
        "usuarioId": USUARIO_ID,
        "frameBase64": frame_base64,
        "resetarSessao": resetar_sessao
    }
    
    try:
        response = requests.post(API_URL, headers=headers, json=payload)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        print(f"❌ Erro ao enviar frame: {e}")
        return None

def exibir_resultado(resultado):
    """Exibe resultado formatado"""
    if not resultado:
        return
    
    print("\n" + "="*50)
    print("📊 RESULTADO DO MONITORAMENTO")
    print("="*50)
    print(f"Movimento: {'✅ Detectado' if resultado['movimentoDetectado'] else '❌ Não detectado'}")
    print(f"Quantidade de Movimento: {resultado['quantidadeMovimento']} pixels")
    print(f"Presente: {'✅ Sim' if resultado['presente'] else '❌ Não'}")
    print(f"Tempo Sentado: {resultado['tempoSentadoMinutos']} minutos")
    print(f"Total de Pausas: {resultado['totalPausas']}")
    print(f"Mensagem: {resultado['mensagem']}")
    
    if resultado.get('sugerirAlongamento'):
        print("\n💡 SUGESTÕES DE ALONGAMENTO:")
        for sugestao in resultado.get('sugestoes', []):
            print(f"  - {sugestao}")
    print("="*50 + "\n")

def main():
    print("🚀 Iniciando monitoramento de pausas...")
    print(f"📡 Enviando frames a cada {INTERVALO_SEGUNDOS} segundos")
    print("Pressione Ctrl+C para parar\n")
    
    try:
        # Primeiro frame (sempre detecta movimento)
        print("📸 Capturando primeiro frame...")
        frame = capturar_frame()
        resultado = enviar_frame(frame, resetar_sessao=True)
        exibir_resultado(resultado)
        
        # Loop contínuo
        contador = 1
        while True:
            time.sleep(INTERVALO_SEGUNDOS)
            contador += 1
            
            print(f"📸 Capturando frame #{contador}...")
            frame = capturar_frame()
            resultado = enviar_frame(frame)
            exibir_resultado(resultado)
            
    except KeyboardInterrupt:
        print("\n\n⏹️ Monitoramento interrompido pelo usuário")
    except Exception as e:
        print(f"\n❌ Erro: {e}")

if __name__ == "__main__":
    main()
```

**Como usar:**
1. Instale as dependências:
   ```bash
   pip install opencv-python requests
   ```

2. Edite o script e coloque seu token JWT

3. Execute:
   ```bash
   python teste_pausa_monitor.py
   ```

---

## 🧪 Método 5: Teste com Swagger UI

1. Acesse: `http://localhost:8080/swagger-ui.html`
2. Faça login primeiro (endpoint `/api/auth/login`)
3. Copie o token retornado
4. Clique em **Authorize** (cadeado no topo)
5. Cole o token: `Bearer seu-token-aqui`
6. Vá para o endpoint `/ia/pausa-monitor`
7. Clique em **Try it out**
8. Preencha o JSON:
   ```json
   {
     "usuarioId": 1,
     "frameBase64": "iVBORw0KGgoAAAANS..."
   }
   ```
9. Clique em **Execute**

---

## 📝 Exemplos de Testes

### Teste 1: Primeiro Frame (Sempre Detecta Movimento)
```bash
# Primeiro frame sempre retorna movimentoDetectado: true
# porque não há frame anterior para comparar
```

### Teste 2: Frame Idêntico (Sem Movimento)
```bash
# Envie o mesmo frame duas vezes
# Segunda vez deve retornar movimentoDetectado: false
```

### Teste 3: Frames Diferentes (Com Movimento)
```bash
# Envie frames diferentes
# Deve detectar movimento
```

### Teste 4: Simular Ausência (5 minutos sem movimento)
```bash
# Envie o mesmo frame repetidamente por mais de 5 minutos
# Deve detectar ausência após 5 minutos
```

### Teste 5: Simular Tempo Sentado (1 hora)
```bash
# Envie frames por mais de 1 hora
# Deve sugerir alongamento após 1 hora
```

### Teste 6: Resetar Sessão
```bash
# Envie resetarSessao: true
# Deve resetar todas as estatísticas
```

---

## 🔍 Verificando Logs

Monitore os logs da aplicação para ver o processamento:

```bash
# Logs esperados:
📹 Monitoramento de pausa: Processando frame para usuário 1
Processando frame para usuário 1 - Tamanho: 45678 bytes
Diferença detectada: 25000 pixels - Movimento: true
✅ Usuário 1 retornou após 5 minutos ausente
```

---

## ❓ Troubleshooting

### Erro: "Frame base64 inválido"
- Verifique se o base64 está completo
- Certifique-se de remover o prefixo `data:image/jpeg;base64,` se presente

### Erro: "Usuário não encontrado"
- Verifique se o `usuarioId` existe no banco
- Faça login primeiro para garantir que o usuário existe

### Erro: "401 Unauthorized"
- Verifique se o token JWT está válido
- Faça login novamente para obter novo token

### Movimento sempre detectado como `false`
- Verifique se está enviando frames diferentes
- O primeiro frame sempre retorna `true` (não há comparação)

### Não detecta ausência após 5 minutos
- Verifique se está enviando frames continuamente
- O sistema só detecta ausência se não houver movimento por 5 minutos consecutivos

---

## ✅ Checklist de Testes

- [ ] Login e obtenção de token JWT
- [ ] Conversão de imagem para base64
- [ ] Envio de primeiro frame (deve detectar movimento)
- [ ] Envio de frame idêntico (não deve detectar movimento)
- [ ] Envio de frames diferentes (deve detectar movimento)
- [ ] Resetar sessão funciona
- [ ] Sugestões de alongamento aparecem após 1 hora
- [ ] Ausência detectada após 5 minutos sem movimento
- [ ] Pausas registradas quando usuário retorna

---

**Pronto! Agora você pode testar o monitoramento de pausas de várias formas! 🚀**


