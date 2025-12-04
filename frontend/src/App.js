import './App.css'
import {BrowserRouter as Router, Routes, Route} from 'react-router-dom' // Allows for routing through different page files
import {Home} from './pages/Home'
import {Layout} from './Layout';
import {Artist} from "./pages/music-pages/Artist";
import {Album} from "./pages/music-pages/Album";
import {Track} from "./pages/music-pages/Track";
import {QueryClient, QueryClientProvider} from '@tanstack/react-query';

import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
import {Login} from "./pages/Login";
import {Register} from "./pages/Register";
import {AuthProvider} from "./AuthContext";
import {User} from "./pages/User";
import {UserRatings} from "./pages/UserRatings";
import {UserActivity} from "./pages/UserActivity";
import {Settings} from "./pages/Settings";
import {SpotifyAuthProvider} from "./SpotifyAuthContext";
import GlobalImageErrorHandler from "./GlobalImageErrorHandler";

function App() {
    const queryClient = new QueryClient();

    return (
        <AuthProvider>
            <QueryClientProvider client={queryClient}>
                <SpotifyAuthProvider>
                    <GlobalImageErrorHandler fallbackSrc="/missing.jpg"/>
                    <Router>
                        <Layout/>
                        <Routes>
                            <Route path="/" element={<Home/>}/>
                            <Route path="/artist/:id" element={<Artist/>}/>
                            <Route path="/album/:id" element={<Album/>}/>
                            <Route path="/track/:id" element={<Track/>}/>
                            <Route path="/login" element={<Login/>}/>
                            <Route path="/user/:username" element={<User/>}/>
                            <Route path="/register" element={<Register/>}/>
                            <Route path="/user/:username/ratings/:page" element={<UserRatings/>}/>
                            <Route path="/user/:username/activity/:page" element={<UserActivity/>}/>
                            <Route path="/settings" element={<Settings/>}/>
                            <Route path="/callback" element={<Settings/>}/>
                        </Routes>
                    </Router>
                    <ReactQueryDevtools/>
                </SpotifyAuthProvider>
            </QueryClientProvider>
        </AuthProvider>
    )

}

export default App