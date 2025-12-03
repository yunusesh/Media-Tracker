# Media Tracker

A **work in progress** website that tracks digital **music** consumption.  
Rate, track, review, and view your listening stats.


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

3. **Set up PostgreSQL server**

   Run SQL script
   
4. **Configure environment**

      application.properties (backend)

      .env (frontend/backend as needed)

### Images
![Screenshot 1](/screenshots/Screenshot%201.png "Track Topster")
![Screenshot 1](/screenshots/Screenshot%202.png "Track Topster")
![Screenshot 1](/screenshots/Screenshot%203.png "Track Topster")
![Screenshot 1](/screenshots/Screenshot%204.png "Track Topster")
![Screenshot 1](/screenshots/Screenshot%205.png "Track Topster")
![Screenshot 1](/screenshots/Screenshot%206.png "Track Topster")
![Screenshot 1](/screenshots/Screenshot%207.png "Track Topster")
![Screenshot 1](/screenshots/Screenshot%208.png "Track Topster")
![Screenshot 1](/screenshots/Screenshot%209.png "Track Topster")
![Screenshot 1](/screenshots/Screenshot%2010.png "Track Topster")
