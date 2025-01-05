# Task Manager API

## Proje Açıklaması
Task Manager API, kullanıcıların görev oluşturabileceği, mevcut görevlerini görüntüleyebileceği ve yeni bir hesap oluşturabileceği bir platform sağlar. Bu proje, MongoDB kullanarak görev ve kullanıcı verilerini güvenli bir şekilde saklar ve kullanıcı etkileşimlerini kolaylaştıran temel özelliklere sahiptir.

---

## Özellikler

### 1. **Görev Ekleme**
- Kullanıcılar, görev başlığı, açıklaması ve durumu gibi bilgileri API'ye göndererek yeni görevler oluşturabilir.
- Görev oluşturma işlemi başarıyla tamamlandığında, görevin detayları JSON formatında döndürülür.

### 2. **Görevleri Görüntüleme**
- Kullanıcılar, mevcut görevlerini görüntülemek için API'ye bir GET isteği gönderebilir.
- Sunucu, MongoDB'deki görevleri sorgular ve JSON formatında bir liste döndürür.

### 3. **Kayıt Olma**
- Kullanıcılar, bir hesap oluşturmak için gerekli bilgileri sağlar.
- Şifreler, güvenlik amacıyla bcrypt kütüphanesi kullanılarak hash'lenir ve veritabanına kaydedilir.
- Başarılı bir kayıt işleminin ardından kullanıcıya bilgilendirici bir mesaj iletilir.

### 4. **Bildirimler**
- Görev tamamlandığında veya yeni bir görev eklendiğinde, kullanıcıya bildirim gönderilir.
- **Broadcast Receiver** kullanılarak görev durumları dinlenir ve **NotificationManager API** ile bildirimler oluşturulur.

### 5. **Konum Tabanlı Hatırlatıcılar**
- **FusedLocationProvider API** ile yüksek doğruluklu konum bilgisi elde edilir.
- Konum bazlı hatırlatıcılar, GPS, Wi-Fi ve hücresel ağ verilerini birleştirerek etkinleştirilir.

### 6. **Bağlantı ve Senkronizasyon**
- Görevlerin başka cihazlarla paylaşılması ve senkronizasyonu için bağlantı özellikleri sunulmaktadır.
  - **Bluetooth Low Energy (BLE)**: Düşük enerji tüketimiyle cihazlar arası görev aktarımı.
  - **WiFi Bağlantısı**: Ağ üzerinden görevlerin senkronizasyonu.
- **Kullanım Alanı**:
  - Görev verilerini birden fazla cihaz arasında eşitlemek.
  - Paylaşılan cihazlarla görev yönetimini kolaylaştırmak.

### 7. **Kullanıcı Yetkilendirme**
- **Google ve Apple Hesaplarıyla Giriş**:
  - **Firebase Authentication** kullanılarak, kullanıcıların Google hesaplarıyla hızlı ve güvenli bir şekilde giriş yapması sağlanır.
  - **OAuth 2.0** tabanlı doğrulama ile kullanıcı bilgileri güvenli bir şekilde saklanır.
- **Kullanım Alanı**:
  - Kullanıcı oturumlarını yönetmek.
  - Güvenli ve kolay giriş yöntemleri sunmak.

### 8. **Bulut Tabanlı Servis (AI)**
- Görev açıklamalarına dayalı basit yapay zeka önerileri sağlar.
- **Hugging Face API** kullanılarak görev açıklamaları üzerinde duygu analizi yapılır.
- **Google Cloud AI ve OpenAI API Desteği** ile kullanıcıların görevlerini daha etkili bir şekilde planlamasına yardımcı olunur.
- **Kullanım Alanı**:
  - Görev önerisi ve hatırlatma işlevleri.
  - Kullanıcı için en uygun görevleri önererek verimliliği artırmak.

---

## Teknolojiler
- **MongoDB**: Veritabanı yönetimi.
- **Express.js**: API geliştirme.
- **Node.js**: Backend altyapısı.
- **bcrypt**: Şifreleme.
- **Broadcast Receiver**: Bildirim sistemi.
- **FusedLocationProvider API**: Konum tabanlı özellikler.
- **Bluetooth Low Energy (BLE)** ve **WiFi**: Görev paylaşımı ve senkronizasyon.
- **Firebase Authentication**: Kullanıcı yetkilendirme.
- **Hugging Face API**, **Google Cloud AI**, **OpenAI API**: Yapay zeka tabanlı öneriler.

---

## Kurulum
1. **Depoyu klonlayın:**
   ```bash
   git clone https://github.com/CagriBtyon/MobilProje.git
   ```
2. **Gerekli bağımlılıkları yükleyin:**
   ```bash
   TaskManagerAPI dizinine giderek npm install diyin
   ```
4. **Sunucuyu başlatın:**
   ```bash
   TaskManagerAPI dizinine giderek npm start diyin
   ```
5. **API'yi test edin:**
   - Postman veya herhangi bir API istemcisi kullanarak API uç noktalarını test edin.
6. **Uygulamayı başlatın:**
   - Uygulamayı Android Studio benzeri bir ide ile açın ve başlangıç konfigürasyonunu app olarak ayarlayın ve projeyi başlatın. 
---

## API Uç Noktaları

### Görevler
- **POST /tasks**: Yeni bir görev ekler.
- **GET /tasks**: Mevcut görevlerin listesini döndürür.
- **GET /getAllTasks**: Tüm görevleri döndürür.
- **POST /createTask**: Yeni bir görev oluşturur.
- **GET /getTaskById**: ID ile bir görevi döndürür.
- **GET /getTaskByTaskName**: Görev adıyla bir görevi döndürür.
- **PUT /updateTaskByID/:id**: ID ile belirtilen görevi günceller.
- **DELETE /deleteTaskByID**: ID ile belirtilen görevi siler.

### Kullanıcılar
- **POST /users/register**: Yeni bir kullanıcı oluşturur.
- **POST /users/login**: Kullanıcı girişi yapar.


