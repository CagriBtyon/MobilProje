const bodyParser = require('body-parser');
const cors = require('cors');
const taskRoutes = require('./routes/taskRoutes');
require('dotenv').config();

const app = express();

// Veritabanı bağlantısı
connectDB();

// Middleware
app.use(cors());
app.use(bodyParser.json());

// Rotalar
app.use('/api/tasks', taskRoutes);

// Sunucu başlatma
const PORT = process.env.PORT || 5000;
app.listen(PORT, () => {
  console.log(`Sunucu çalışıyor: http://localhost:${PORT}`);
});
