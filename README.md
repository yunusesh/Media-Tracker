# Media Tracker

A **work in progress** website that tracks digital **music**, **movie**, and **TV** consumption by integrating with multiple streaming service APIs.  
> Currently, only the **music** portion is functional.

---

##  Tech Stack

### Backend
- Java  
- Spring Boot  

### Frontend
- JavaScript  
- HTML / CSS  
- React.js  

---

##  How It Works

### Backend
- Connects to the **MusicBrainz API** to retrieve information about artists, albums, and tracks  
- Uses **Caffeine** to cache lookups on the backend for faster fetches from the frontend  
- Connects to the **Spotify API** to get user listening data  
- Sends this information to the frontend for display  
> **Flow:** MusicBrainz API → Backend → Frontend  

### Frontend
- Uses **React Router** for dynamic page rendering  
- Caches recently visited pages locally with **React Query**  

---

##  Installation Guide

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/media-menu.git
   cd media-menu
2. **Install React dependencies**

```bash
npm install react-dom \
            react-icons \
            react-query \
            react-router \
            react-router-dom \
            react-scripts
```
3. **Set up PostgreSQL server**

   Run SQL script
   
4. **Configure environment**

      application.properties (backend)

      .env (frontend/backend as needed)
