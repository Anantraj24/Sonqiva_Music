/**
 * SONQIVA WEB SIMULATOR & INTERACTIVE SHOWCASE
 * Atmospheric Glassmorphism Audio Engine & Reactive UI State Controller
 */

// --- SAMPLE DATA (Offline Audio Catalogue) ---
const INITIAL_SONGS = [
    {
        id: 1,
        title: "Starlight Echoes",
        artist: "Solaris",
        album: "Atmosphere",
        albumId: 101,
        artistId: 201,
        durationMs: 224000,
        formattedDuration: "3:44",
        path: "/storage/emulated/0/Music/Solaris/Starlight_Echoes.mp3",
        folder: "Solaris",
        fileSize: "8.6 MB",
        format: "audio/mp3",
        bitrate: "320 kbps",
        isFavorite: true,
        frequency: 440
    },
    {
        id: 2,
        title: "Midnight Aurora",
        artist: "Solaris",
        album: "Atmosphere",
        albumId: 101,
        artistId: 201,
        durationMs: 198000,
        formattedDuration: "3:18",
        path: "/storage/emulated/0/Music/Solaris/Midnight_Aurora.mp3",
        folder: "Solaris",
        fileSize: "7.8 MB",
        format: "audio/mp3",
        bitrate: "320 kbps",
        isFavorite: false,
        frequency: 523.25
    },
    {
        id: 3,
        title: "Lunar Gravity",
        artist: "Astral Drift",
        album: "Orbital Waves",
        albumId: 102,
        artistId: 202,
        durationMs: 256000,
        formattedDuration: "4:16",
        path: "/storage/emulated/0/Music/Astral/Lunar_Gravity.flac",
        folder: "Astral",
        fileSize: "28.4 MB",
        format: "audio/flac",
        bitrate: "960 kbps (Lossless)",
        isFavorite: true,
        frequency: 392
    },
    {
        id: 4,
        title: "Cosmic Horizon",
        artist: "Astral Drift",
        album: "Orbital Waves",
        albumId: 102,
        artistId: 202,
        durationMs: 212000,
        formattedDuration: "3:32",
        path: "/storage/emulated/0/Music/Astral/Cosmic_Horizon.flac",
        folder: "Astral",
        fileSize: "24.1 MB",
        format: "audio/flac",
        bitrate: "960 kbps (Lossless)",
        isFavorite: false,
        frequency: 349.23
    },
    {
        id: 5,
        title: "Neon Rain",
        artist: "Cyber Pulse",
        album: "Synthetic Dreams",
        albumId: 103,
        artistId: 203,
        durationMs: 185000,
        formattedDuration: "3:05",
        path: "/storage/emulated/0/Music/Synth/Neon_Rain.opus",
        folder: "Synth",
        fileSize: "4.2 MB",
        format: "audio/opus",
        bitrate: "160 kbps",
        isFavorite: false,
        frequency: 587.33
    },
    {
        id: 6,
        title: "Cybernetic Pulse",
        artist: "Cyber Pulse",
        album: "Synthetic Dreams",
        albumId: 103,
        artistId: 203,
        durationMs: 240000,
        formattedDuration: "4:00",
        path: "/storage/emulated/0/Music/Synth/Cybernetic_Pulse.opus",
        folder: "Synth",
        fileSize: "5.1 MB",
        format: "audio/opus",
        bitrate: "160 kbps",
        isFavorite: true,
        frequency: 659.25
    }
];

// --- APP STATE ---
let songs = JSON.parse(localStorage.getItem("sonqiva_songs")) || INITIAL_SONGS;
let playlists = JSON.parse(localStorage.getItem("sonqiva_playlists")) || [
    { id: 1, name: "Chill Atmospheric", songIds: [1, 3] },
    { id: 2, name: "Synthwave Night Drive", songIds: [5, 6] }
];

let playbackState = {
    currentSong: null,
    isPlaying: false,
    currentPositionMs: 0,
    durationMs: 0,
    playbackSpeed: 1.0,
    isShuffleEnabled: false,
    repeatMode: "OFF", // OFF, ALL, ONE
    queue: [...songs],
    queueIndex: 0,
    sleepTimerSeconds: null,
    sortOrder: "title_asc"
};

let activeActionSong = null;
let currentFolderContext = null;
let sleepTimerInterval = null;
let audioContext = null;
let synthOscillator = null;
let synthGain = null;
let audioElement = new Audio();
let isSyntheticPlayback = true;

// --- WEB AUDIO SYNTHESIZER (Simulates rich audio tones when no MP3 loaded) ---
function initAudio() {
    if (!audioContext) {
        audioContext = new (window.AudioContext || window.webkitAudioContext)();
    }
    if (audioContext.state === 'suspended') {
        audioContext.resume();
    }
}

function playSyntheticTone(frequency) {
    stopSyntheticTone();
    initAudio();
    synthOscillator = audioContext.createOscillator();
    synthGain = audioContext.createGain();
    
    synthOscillator.type = 'sine';
    synthOscillator.frequency.setValueAtTime(frequency || 440, audioContext.currentTime);
    
    // Soft ambient swell
    synthGain.gain.setValueAtTime(0.01, audioContext.currentTime);
    synthGain.gain.exponentialRampToValueAtTime(0.12, audioContext.currentTime + 0.5);

    synthOscillator.connect(synthGain);
    synthGain.connect(audioContext.destination);
    synthOscillator.start();
}

function stopSyntheticTone() {
    if (synthGain && audioContext) {
        synthGain.gain.setValueAtTime(synthGain.gain.value, audioContext.currentTime);
        synthGain.gain.exponentialRampToValueAtTime(0.0001, audioContext.currentTime + 0.1);
        setTimeout(() => {
            if (synthOscillator) {
                try { synthOscillator.stop(); } catch(e){}
                synthOscillator.disconnect();
                synthOscillator = null;
            }
        }, 120);
    }
}

// --- CORE PLAYBACK CONTROLLER ---
function playSong(song) {
    if (!song) return;
    initAudio();
    playbackState.currentSong = song;
    playbackState.isPlaying = true;
    playbackState.durationMs = song.durationMs || 180000;
    playbackState.currentPositionMs = 0;

    const existingIdx = playbackState.queue.findIndex(s => s.id === song.id);
    if (existingIdx !== -1) {
        playbackState.queueIndex = existingIdx;
    } else {
        playbackState.queue.push(song);
        playbackState.queueIndex = playbackState.queue.length - 1;
    }

    if (song.audioBlobUrl) {
        isSyntheticPlayback = false;
        stopSyntheticTone();
        audioElement.src = song.audioBlobUrl;
        audioElement.playbackRate = playbackState.playbackSpeed;
        audioElement.play().catch(() => {});
    } else {
        isSyntheticPlayback = true;
        audioElement.pause();
        playSyntheticTone(song.frequency || 440);
    }

    updateUI();
}

function togglePlayPause() {
    if (!playbackState.currentSong) {
        if (songs.length > 0) playSong(songs[0]);
        return;
    }

    playbackState.isPlaying = !playbackState.isPlaying;

    if (playbackState.isPlaying) {
        if (isSyntheticPlayback) {
            playSyntheticTone(playbackState.currentSong.frequency);
        } else {
            audioElement.play().catch(() => {});
        }
    } else {
        if (isSyntheticPlayback) {
            stopSyntheticTone();
        } else {
            audioElement.pause();
        }
    }
    updateUI();
}

function skipNext() {
    if (playbackState.queue.length === 0) return;
    let nextIdx = playbackState.queueIndex + 1;
    if (nextIdx >= playbackState.queue.length) {
        if (playbackState.repeatMode === "ALL") nextIdx = 0;
        else return;
    }
    playbackState.queueIndex = nextIdx;
    playSong(playbackState.queue[nextIdx]);
}

function skipPrevious() {
    if (playbackState.queue.length === 0) return;
    let prevIdx = playbackState.queueIndex - 1;
    if (prevIdx < 0) {
        prevIdx = playbackState.repeatMode === "ALL" ? playbackState.queue.length - 1 : 0;
    }
    playbackState.queueIndex = prevIdx;
    playSong(playbackState.queue[prevIdx]);
}

function seekToMs(targetMs) {
    playbackState.currentPositionMs = targetMs;
    if (!isSyntheticPlayback) {
        audioElement.currentTime = targetMs / 1000;
    }
    updateSeekUI();
}

function setPlaybackSpeed(speed) {
    playbackState.playbackSpeed = speed;
    if (!isSyntheticPlayback) {
        audioElement.playbackRate = speed;
    }
    updateUI();
}

function toggleShuffle() {
    playbackState.isShuffleEnabled = !playbackState.isShuffleEnabled;
    if (playbackState.isShuffleEnabled) {
        playbackState.queue = [...playbackState.queue].sort(() => Math.random() - 0.5);
    } else {
        playbackState.queue = [...songs];
    }
    updateUI();
}

function toggleRepeat() {
    if (playbackState.repeatMode === "OFF") playbackState.repeatMode = "ALL";
    else if (playbackState.repeatMode === "ALL") playbackState.repeatMode = "ONE";
    else playbackState.repeatMode = "OFF";
    updateUI();
}

function startSleepTimer(minutes) {
    clearInterval(sleepTimerInterval);
    if (!minutes || minutes <= 0) {
        cancelSleepTimer();
        return;
    }
    playbackState.sleepTimerSeconds = minutes * 60;
    updateUI();

    sleepTimerInterval = setInterval(() => {
        if (playbackState.sleepTimerSeconds > 0) {
            playbackState.sleepTimerSeconds--;
            updateUI();
        } else {
            clearInterval(sleepTimerInterval);
            playbackState.sleepTimerSeconds = null;
            if (playbackState.isPlaying) togglePlayPause();
            updateUI();
        }
    }, 1000);
}

function cancelSleepTimer() {
    clearInterval(sleepTimerInterval);
    playbackState.sleepTimerSeconds = null;
    updateUI();
}

function toggleFavorite(song) {
    song.isFavorite = !song.isFavorite;
    localStorage.setItem("sonqiva_songs", JSON.stringify(songs));
    updateUI();
}

// --- TICK PROGRESS LOOP ---
setInterval(() => {
    if (playbackState.isPlaying) {
        if (!isSyntheticPlayback) {
            playbackState.currentPositionMs = audioElement.currentTime * 1000;
            playbackState.durationMs = audioElement.duration * 1000 || playbackState.durationMs;
        } else {
            playbackState.currentPositionMs += (1000 * playbackState.playbackSpeed);
            if (playbackState.currentPositionMs >= playbackState.durationMs) {
                if (playbackState.repeatMode === "ONE") {
                    seekToMs(0);
                } else {
                    skipNext();
                }
            }
        }
        updateSeekUI();
    }
}, 500);

// --- HELPERS ---
function formatMs(ms) {
    const totalSec = Math.floor((ms || 0) / 1000);
    const mins = Math.floor(totalSec / 60);
    const secs = totalSec % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
}

function getUniqueAlbums() {
    const map = new Map();
    songs.forEach(s => {
        if (!map.has(s.albumId)) {
            map.set(s.albumId, { id: s.albumId, title: s.album, artist: s.artist, songs: [] });
        }
        map.get(s.albumId).songs.push(s);
    });
    return Array.from(map.values());
}

function getUniqueArtists() {
    const map = new Map();
    songs.forEach(s => {
        if (!map.has(s.artistId)) {
            map.set(s.artistId, { id: s.artistId, name: s.artist, songs: [], albumCount: 0 });
        }
        map.get(s.artistId).songs.push(s);
    });
    return Array.from(map.values()).map(a => ({
        ...a,
        albumCount: new Set(a.songs.map(s => s.albumId)).size
    }));
}

function getUniqueFolders() {
    const map = new Map();
    songs.forEach(s => {
        const folder = s.folder || "Default";
        if (!map.has(folder)) {
            map.set(folder, { name: folder, path: s.path.substring(0, s.path.lastIndexOf('/')), songs: [] });
        }
        map.get(folder).songs.push(s);
    });
    return Array.from(map.values());
}

// --- UI RENDERING & SYNCHRONIZATION ---
function updateUI() {
    // 1. Counters
    const favs = songs.filter(s => s.isFavorite);
    const albums = getUniqueAlbums();
    const artists = getUniqueArtists();

    document.getElementById("count-songs").innerText = songs.length;
    document.getElementById("count-favs").innerText = favs.length;
    document.getElementById("count-albums").innerText = albums.length;
    document.getElementById("count-artists").innerText = artists.length;
    document.getElementById("count-playlists").innerText = playlists.length;
    document.getElementById("queue-count").innerText = playbackState.queue.length;

    // 2. Playback State buttons
    const playIcon = playbackState.isPlaying ? "pause" : "play_arrow";
    document.getElementById("mini-play-btn").innerHTML = `<span class="material-symbols-rounded">${playIcon}</span>`;
    document.getElementById("player-play-btn").innerHTML = `<span class="material-symbols-rounded">${playIcon}</span>`;
    
    // 3. Mini Player Info
    if (playbackState.currentSong) {
        document.getElementById("mini-title").innerText = playbackState.currentSong.title;
        document.getElementById("mini-artist").innerText = playbackState.currentSong.artist;
        document.getElementById("player-title").innerText = playbackState.currentSong.title;
        document.getElementById("player-artist").innerText = playbackState.currentSong.artist;
        
        // Favorite button in full player
        const isFav = playbackState.currentSong.isFavorite;
        document.getElementById("player-fav-btn").innerHTML = `<span class="material-symbols-rounded" style="color: ${isFav ? 'var(--primary-accent)' : 'inherit'}">${isFav ? 'favorite' : 'favorite_border'}</span>`;
    }

    // 4. Repeat / Shuffle / Speed / Sleep controls in Full Player
    const shuffleBtn = document.getElementById("player-shuffle-btn");
    shuffleBtn.style.color = playbackState.isShuffleEnabled ? "var(--primary-accent)" : "var(--on-surface-variant)";

    const repeatBtn = document.getElementById("player-repeat-btn");
    repeatBtn.innerHTML = `<span class="material-symbols-rounded">${playbackState.repeatMode === "ONE" ? "repeat_one" : "repeat"}</span>`;
    repeatBtn.style.color = playbackState.repeatMode !== "OFF" ? "var(--primary-accent)" : "var(--on-surface-variant)";

    document.getElementById("speed-label").innerText = `${playbackState.playbackSpeed}x`;

    const sleepLabel = document.getElementById("sleep-label");
    const openSleepBtn = document.getElementById("open-sleep-btn");
    if (playbackState.sleepTimerSeconds !== null) {
        const mins = Math.floor(playbackState.sleepTimerSeconds / 60);
        const secs = playbackState.sleepTimerSeconds % 60;
        sleepLabel.innerText = `${mins}:${secs.toString().padStart(2, '0')}`;
        openSleepBtn.classList.add("active");
        document.getElementById("cancel-sleep-btn").style.display = "flex";
    } else {
        sleepLabel.innerText = "Timer";
        openSleepBtn.classList.remove("active");
        document.getElementById("cancel-sleep-btn").style.display = "none";
    }

    // 5. Render Lists
    renderHome();
    renderLibrary();
    renderFolders();
    renderSearch();
    renderQueue();
}

function updateSeekUI() {
    const cur = playbackState.currentPositionMs;
    const dur = playbackState.durationMs || 1;
    const progress = Math.min(100, Math.max(0, (cur / dur) * 100));

    document.getElementById("mini-progress-fill").style.width = `${progress}%`;
    document.getElementById("seek-slider").value = progress;
    document.getElementById("time-current").innerText = formatMs(cur);
    document.getElementById("time-duration").innerText = formatMs(dur);
}

// --- RENDER SCREEN VIEWS ---
function createSongRow(song) {
    const isCurrent = playbackState.currentSong && playbackState.currentSong.id === song.id;
    const isPlaying = isCurrent && playbackState.isPlaying;

    const div = document.createElement("div");
    div.className = `song-row ${isCurrent ? 'active' : ''}`;
    div.setAttribute("role", "group");
    div.setAttribute("tabindex", "0");
    div.setAttribute("aria-label", `Play ${song.title} by ${song.artist}`);
    div.innerHTML = `
        <div class="song-art-mini">
            <span class="material-symbols-rounded">${isPlaying ? 'equalizer' : 'music_note'}</span>
        </div>
        <div class="song-meta">
            <div class="song-name">${song.title}</div>
            <div class="song-artist-album">${song.artist} • ${song.album}</div>
        </div>
        <div class="song-duration">${song.formattedDuration}</div>
        <button class="song-action-btn" data-song-id="${song.id}" aria-label="More options for ${song.title}">
            <span class="material-symbols-rounded">more_vert</span>
        </button>
    `;

    div.addEventListener("click", (e) => {
        if (e.target.closest(".song-action-btn")) {
            e.stopPropagation();
            openActionSheet(song);
        } else {
            playSong(song);
        }
    });
    div.addEventListener("keydown", (e) => {
        if (e.target.closest(".song-action-btn")) return;
        if (e.key === "Enter" || e.key === " ") {
            e.preventDefault();
            playSong(song);
        }
    });

    return div;
}

function renderHome() {
    // Continue listening
    if (playbackState.currentSong) {
        document.getElementById("continue-title").innerText = playbackState.currentSong.title;
        document.getElementById("continue-artist").innerText = playbackState.currentSong.artist;
    }

    // Albums carousel
    const carousel = document.getElementById("home-albums-carousel");
    carousel.innerHTML = "";
    getUniqueAlbums().forEach(album => {
        const item = document.createElement("div");
        item.className = "album-card-small";
        item.innerHTML = `
            <div class="album-art-box">
                <span class="material-symbols-rounded" style="font-size: 40px;">album</span>
            </div>
            <div class="album-name">${album.title}</div>
            <div class="album-sub">${album.artist}</div>
        `;
        item.addEventListener("click", () => openDetailView("album", album));
        carousel.appendChild(item);
    });

    // Recent songs
    const recent = document.getElementById("home-recent-songs");
    recent.innerHTML = "";
    songs.slice(0, 4).forEach(song => {
        recent.appendChild(createSongRow(song));
    });
}

function renderLibrary() {
    // Songs Tab
    const songsList = document.getElementById("library-songs-list");
    songsList.innerHTML = "";
    let sortedSongs = [...songs];
    if (playbackState.sortOrder === "title_asc") sortedSongs.sort((a, b) => a.title.localeCompare(b.title));
    else if (playbackState.sortOrder === "title_desc") sortedSongs.sort((a, b) => b.title.localeCompare(a.title));
    else if (playbackState.sortOrder === "artist_asc") sortedSongs.sort((a, b) => a.artist.localeCompare(b.artist));
    else if (playbackState.sortOrder === "duration_desc") sortedSongs.sort((a, b) => b.durationMs - a.durationMs);
    else if (playbackState.sortOrder === "duration_asc") sortedSongs.sort((a, b) => a.durationMs - b.durationMs);

    sortedSongs.forEach(song => songsList.appendChild(createSongRow(song)));

    // Favorites Tab
    const favsList = document.getElementById("library-favorites-list");
    favsList.innerHTML = "";
    const favSongs = songs.filter(s => s.isFavorite);
    if (favSongs.length === 0) {
        favsList.innerHTML = `<div style="text-align:center; padding: 32px; color: var(--on-surface-variant);">No favorite tracks yet.<br>Tap more options on any track to add it here.</div>`;
    } else {
        favSongs.forEach(song => favsList.appendChild(createSongRow(song)));
    }

    // Albums Grid
    const albumsGrid = document.getElementById("library-albums-grid");
    albumsGrid.innerHTML = "";
    getUniqueAlbums().forEach(album => {
        const item = document.createElement("div");
        item.className = "album-card-small";
        item.style.width = "100%";
        item.innerHTML = `
            <div class="album-art-box" style="width: 100%; aspect-ratio: 1; height: auto;">
                <span class="material-symbols-rounded" style="font-size: 48px;">album</span>
            </div>
            <div class="album-name">${album.title}</div>
            <div class="album-sub">${album.artist} • ${album.songs.length} Tracks</div>
        `;
        item.addEventListener("click", () => openDetailView("album", album));
        albumsGrid.appendChild(item);
    });

    // Artists Grid
    const artistsGrid = document.getElementById("library-artists-grid");
    artistsGrid.innerHTML = "";
    getUniqueArtists().forEach(artist => {
        const item = document.createElement("div");
        item.className = "artist-item-box";
        item.innerHTML = `
            <div class="artist-avatar">
                <span class="material-symbols-rounded" style="font-size: 36px;">person</span>
            </div>
            <div class="artist-name-label">${artist.name}</div>
            <div class="artist-sub-label">${artist.songs.length} Songs</div>
        `;
        item.addEventListener("click", () => openDetailView("artist", artist));
        artistsGrid.appendChild(item);
    });

    // Playlists Tab
    const playlistsList = document.getElementById("library-playlists-list");
    playlistsList.innerHTML = "";
    playlists.forEach(pl => {
        const item = document.createElement("div");
        item.className = "folder-item glass";
        item.innerHTML = `
            <div class="folder-icon-box">
                <span class="material-symbols-rounded">queue_music</span>
            </div>
            <div style="flex:1;">
                <div style="font-weight:600; font-size:0.9rem;">${pl.name}</div>
                <div style="font-size:0.75rem; color:var(--on-surface-variant);">${pl.songIds.length} Songs</div>
            </div>
            <span class="material-symbols-rounded" style="color:var(--on-surface-variant);">chevron_right</span>
        `;
        item.addEventListener("click", () => openDetailView("playlist", pl));
        playlistsList.appendChild(item);
    });
}

function renderFolders() {
    const container = document.getElementById("folders-container");
    container.innerHTML = "";

    if (!currentFolderContext) {
        document.getElementById("folder-back-btn").style.display = "none";
        document.getElementById("folder-path-label").innerText = "Internal Storage";
        getUniqueFolders().forEach(folder => {
            const item = document.createElement("div");
            item.className = "folder-item glass";
            item.innerHTML = `
                <div class="folder-icon-box">
                    <span class="material-symbols-rounded">folder</span>
                </div>
                <div style="flex:1;">
                    <div style="font-weight:600; font-size:0.9rem;">${folder.name}</div>
                    <div style="font-size:0.75rem; color:var(--on-surface-variant);">${folder.songs.length} audio tracks</div>
                </div>
                <span class="material-symbols-rounded" style="color:var(--on-surface-variant);">chevron_right</span>
            `;
            item.addEventListener("click", () => {
                currentFolderContext = folder;
                renderFolders();
            });
            container.appendChild(item);
        });
    } else {
        document.getElementById("folder-back-btn").style.display = "flex";
        document.getElementById("folder-path-label").innerText = currentFolderContext.path;
        currentFolderContext.songs.forEach(song => {
            container.appendChild(createSongRow(song));
        });
    }
}

function renderSearch() {
    const query = document.getElementById("search-input").value.trim().toLowerCase();
    const activeFilter = document.querySelector(".filter-chip.active")?.dataset.filter || "all";
    const container = document.getElementById("search-results");
    container.innerHTML = "";

    if (!query) {
        container.innerHTML = `
            <div style="text-align:center; padding: 48px 24px; color: var(--on-surface-variant);">
                <span class="material-symbols-rounded" style="font-size: 48px; color: var(--primary-accent); margin-bottom: 8px;">search</span>
                <h4 style="color: var(--on-surface); margin-bottom: 4px;">Find Any Music Instantly</h4>
                <p style="font-size: 0.78rem;">Search across all offline songs, artists, and albums.</p>
            </div>
        `;
        return;
    }

    const matchSongs = songs.filter(s => s.title.toLowerCase().includes(query) || s.artist.toLowerCase().includes(query) || s.album.toLowerCase().includes(query));
    const matchArtists = getUniqueArtists().filter(a => a.name.toLowerCase().includes(query));
    const matchAlbums = getUniqueAlbums().filter(a => a.title.toLowerCase().includes(query) || a.artist.toLowerCase().includes(query));

    if (matchSongs.length === 0 && matchArtists.length === 0 && matchAlbums.length === 0) {
        container.innerHTML = `<div style="text-align:center; padding: 40px; color: var(--on-surface-variant);">No results found for "${query}"</div>`;
        return;
    }

    // Artists section
    if ((activeFilter === "all" || activeFilter === "artists") && matchArtists.length > 0) {
        const title = document.createElement("h4");
        title.className = "section-title";
        title.innerText = `Artists (${matchArtists.length})`;
        container.appendChild(title);

        const row = document.createElement("div");
        row.className = "grid-3col";
        row.style.marginBottom = "16px";
        matchArtists.forEach(artist => {
            const item = document.createElement("div");
            item.className = "artist-item-box";
            item.innerHTML = `
                <div class="artist-avatar" style="width:60px; height:60px;">
                    <span class="material-symbols-rounded">person</span>
                </div>
                <div class="artist-name-label">${artist.name}</div>
            `;
            item.addEventListener("click", () => openDetailView("artist", artist));
            row.appendChild(item);
        });
        container.appendChild(row);
    }

    // Albums section
    if ((activeFilter === "all" || activeFilter === "albums") && matchAlbums.length > 0) {
        const title = document.createElement("h4");
        title.className = "section-title";
        title.innerText = `Albums (${matchAlbums.length})`;
        container.appendChild(title);

        const row = document.createElement("div");
        row.className = "carousel-container";
        row.style.marginBottom = "16px";
        matchAlbums.forEach(album => {
            const item = document.createElement("div");
            item.className = "album-card-small";
            item.innerHTML = `
                <div class="album-art-box" style="width:100px; height:100px;">
                    <span class="material-symbols-rounded">album</span>
                </div>
                <div class="album-name">${album.title}</div>
            `;
            item.addEventListener("click", () => openDetailView("album", album));
            row.appendChild(item);
        });
        container.appendChild(row);
    }

    // Songs section
    if ((activeFilter === "all" || activeFilter === "songs") && matchSongs.length > 0) {
        const title = document.createElement("h4");
        title.className = "section-title";
        title.innerText = `Songs (${matchSongs.length})`;
        container.appendChild(title);

        matchSongs.forEach(song => container.appendChild(createSongRow(song)));
    }
}

function renderQueue() {
    const list = document.getElementById("queue-list-container");
    list.innerHTML = "";
    playbackState.queue.forEach((song, idx) => {
        const isCurrent = idx === playbackState.queueIndex;
        const item = document.createElement("div");
        item.className = `song-row ${isCurrent ? 'active' : ''}`;
        item.innerHTML = `
            <div style="font-weight:700; font-size:0.8rem; color:${isCurrent ? 'var(--primary-accent)' : 'var(--on-surface-variant)'}; width:20px;">
                ${idx + 1}
            </div>
            <div class="song-meta">
                <div class="song-name">${song.title}</div>
                <div class="song-artist-album">${song.artist}</div>
            </div>
            <div class="song-duration">${song.formattedDuration}</div>
        `;
        item.addEventListener("click", () => {
            playbackState.queueIndex = idx;
            playSong(song);
            closeAllSheets();
        });
        list.appendChild(item);
    });
}

function openDetailView(type, data) {
    document.getElementById("detail-type-badge").innerText = type.toUpperCase();
    let detailSongs = [];

    if (type === "album") {
        document.getElementById("detail-title").innerText = data.title;
        document.getElementById("detail-subtitle").innerText = `${data.artist} • ${data.songs.length} Tracks`;
        detailSongs = data.songs;
    } else if (type === "artist") {
        document.getElementById("detail-title").innerText = data.name;
        document.getElementById("detail-subtitle").innerText = `${data.albumCount} Albums • ${data.songs.length} Songs`;
        detailSongs = data.songs;
    } else if (type === "playlist") {
        document.getElementById("detail-title").innerText = data.name;
        detailSongs = songs.filter(s => data.songIds.includes(s.id));
        document.getElementById("detail-subtitle").innerText = `Custom Playlist • ${detailSongs.length} Tracks`;
    }

    const songListContainer = document.getElementById("detail-song-list");
    songListContainer.innerHTML = "";
    detailSongs.forEach(s => songListContainer.appendChild(createSongRow(s)));

    document.getElementById("detail-play-btn").onclick = () => {
        if (detailSongs.length > 0) {
            playbackState.queue = [...detailSongs];
            playSong(detailSongs[0]);
        }
    };

    document.getElementById("detail-shuffle-btn").onclick = () => {
        if (detailSongs.length > 0) {
            const shuffled = [...detailSongs].sort(() => Math.random() - 0.5);
            playbackState.queue = shuffled;
            playSong(shuffled[0]);
        }
    };

    switchScreen("screen-detail");
}

function switchScreen(screenId) {
    document.querySelectorAll(".screen").forEach(s => s.classList.remove("active"));
    const target = document.getElementById(screenId);
    if (target) target.classList.add("active");

    document.querySelectorAll(".nav-item").forEach(item => {
        item.classList.toggle("active", item.dataset.screen === screenId);
    });
}

// --- MODALS & BOTTOM SHEETS ---
let lastFocusedElement = null;

function openSheet(id) {
    const overlay = document.getElementById(id);
    lastFocusedElement = document.activeElement;
    overlay.classList.add("open");
    const focusables = overlay.querySelectorAll(
        'button, [href], input, select, textarea, [role="button"], [tabindex]:not([tabindex="-1"])'
    );
    const first = focusables[0];
    if (first) first.focus();
    else {
        overlay.setAttribute("tabindex", "-1");
        overlay.focus();
    }
}

function openActionSheet(song) {
    activeActionSong = song;
    document.getElementById("sheet-song-title").innerText = song.title;
    document.getElementById("sheet-song-artist").innerText = `${song.artist} • ${song.album}`;
    openSheet("actions-sheet");
}

function closeAllSheets() {
    const hadOpen = document.querySelector(".bottom-sheet-overlay.open") !== null ||
        document.getElementById("full-player-modal").classList.contains("open");
    document.querySelectorAll(".bottom-sheet-overlay").forEach(el => el.classList.remove("open"));
    document.getElementById("full-player-modal").classList.remove("open");
    if (hadOpen && lastFocusedElement && document.contains(lastFocusedElement)) {
        lastFocusedElement.focus();
    }
    lastFocusedElement = null;
}

// --- EVENT BINDINGS ---
document.addEventListener("DOMContentLoaded", () => {
    // Navigation
    document.querySelectorAll(".nav-item").forEach(item => {
        item.addEventListener("click", () => {
            if (item.dataset.screen === "screen-folders") currentFolderContext = null;
            switchScreen(item.dataset.screen);
        });
    });

    document.getElementById("home-search-btn").addEventListener("click", () => switchScreen("screen-search"));
    document.getElementById("detail-back-btn").addEventListener("click", () => switchScreen("screen-library"));
    document.getElementById("folder-back-btn").addEventListener("click", () => {
        currentFolderContext = null;
        renderFolders();
    });

    // Library Tabs
    const tabs = Array.from(document.querySelectorAll(".tab-item"));
    const setTabState = (el, on) => {
        el.classList.toggle("active", on);
        el.setAttribute("aria-selected", on ? "true" : "false");
        el.tabIndex = on ? 0 : -1;
    };
    const activateTab = (tab, shouldFocus = true) => {
        tabs.forEach(t => setTabState(t, t === tab));
        document.querySelectorAll(".tab-panel").forEach(p => p.classList.remove("active"));
        document.getElementById(`panel-${tab.dataset.tab}`).classList.add("active");
        if (shouldFocus) tab.focus();
    };
    tabs.forEach(tab => {
        tab.addEventListener("click", () => activateTab(tab));
        tab.addEventListener("keydown", (e) => {
            const idx = tabs.indexOf(tab);
            let next = -1;
            if (e.key === "ArrowRight") next = (idx + 1) % tabs.length;
            else if (e.key === "ArrowLeft") next = (idx - 1 + tabs.length) % tabs.length;
            else if (e.key === "Home") next = 0;
            else if (e.key === "End") next = tabs.length - 1;
            else if (e.key === "Enter" || e.key === " ") { e.preventDefault(); activateTab(tab); return; }
            else return;
            e.preventDefault();
            tabs[next].focus();
        });
    });
    activateTab(document.querySelector(".tab-item.active"), false);

    // Search input
    const searchInput = document.getElementById("search-input");
    const clearSearch = document.getElementById("search-clear-btn");
    searchInput.addEventListener("input", () => {
        clearSearch.style.display = searchInput.value ? "flex" : "none";
        renderSearch();
    });
    clearSearch.addEventListener("click", () => {
        searchInput.value = "";
        clearSearch.style.display = "none";
        renderSearch();
    });

    document.querySelectorAll(".filter-chip").forEach(chip => {
        chip.setAttribute("role", "button");
        chip.setAttribute("tabindex", "0");
        chip.setAttribute("aria-pressed", chip.classList.contains("active") ? "true" : "false");
        chip.addEventListener("click", () => {
            document.querySelectorAll(".filter-chip").forEach(c => c.classList.remove("active"));
            chip.classList.add("active");
            document.querySelectorAll(".filter-chip").forEach(c => c.setAttribute("aria-pressed", c === chip ? "true" : "false"));
            renderSearch();
        });
        chip.addEventListener("keydown", (e) => {
            if (e.key === "Enter" || e.key === " ") { e.preventDefault(); chip.click(); }
        });
    });

    // Playback Controls
    document.getElementById("mini-play-btn").addEventListener("click", (e) => {
        e.stopPropagation();
        togglePlayPause();
    });
    document.getElementById("mini-next-btn").addEventListener("click", (e) => {
        e.stopPropagation();
        skipNext();
    });
    document.getElementById("continue-play-btn").addEventListener("click", togglePlayPause);
    document.getElementById("player-play-btn").addEventListener("click", togglePlayPause);
    document.getElementById("player-next-btn").addEventListener("click", skipNext);
    document.getElementById("player-prev-btn").addEventListener("click", skipPrevious);
    document.getElementById("player-shuffle-btn").addEventListener("click", toggleShuffle);
    document.getElementById("player-repeat-btn").addEventListener("click", toggleRepeat);
    document.getElementById("player-fav-btn").addEventListener("click", () => {
        if (playbackState.currentSong) toggleFavorite(playbackState.currentSong);
    });

    // Seek bar
    const seekSlider = document.getElementById("seek-slider");
    seekSlider.addEventListener("input", () => {
        const targetMs = (seekSlider.value / 100) * (playbackState.durationMs || 1);
        seekToMs(targetMs);
    });

    // Full Player Open/Close
    document.getElementById("mini-player").addEventListener("click", () => {
        openSheet("full-player-modal");
    });
    document.getElementById("collapse-player-btn").addEventListener("click", () => {
        closeAllSheets();
    });

    // Quick Play/Shuffle actions
    document.getElementById("quick-shuffle-btn").addEventListener("click", () => {
        const shuffled = [...songs].sort(() => Math.random() - 0.5);
        playbackState.queue = shuffled;
        playSong(shuffled[0]);
    });
    document.getElementById("quick-favs-btn").addEventListener("click", () => {
        switchScreen("screen-library");
        document.querySelector(`.tab-item[data-tab="favorites"]`).click();
    });
    document.getElementById("lib-play-all-btn").addEventListener("click", () => {
        playbackState.queue = [...songs];
        if (songs.length > 0) playSong(songs[0]);
    });
    document.getElementById("lib-shuffle-btn").addEventListener("click", () => {
        const shuffled = [...songs].sort(() => Math.random() - 0.5);
        playbackState.queue = shuffled;
        if (shuffled.length > 0) playSong(shuffled[0]);
    });
    document.getElementById("favs-play-all-btn").addEventListener("click", () => {
        const favs = songs.filter(s => s.isFavorite);
        if (favs.length > 0) {
            playbackState.queue = [...favs];
            playSong(favs[0]);
        }
    });

    // Bottom Sheets Openers
    document.getElementById("open-speed-btn").addEventListener("click", () => openSheet("speed-sheet"));
    document.getElementById("open-sleep-btn").addEventListener("click", () => openSheet("sleep-sheet"));
    document.getElementById("open-queue-btn").addEventListener("click", () => openSheet("queue-sheet"));
    document.getElementById("open-sort-sheet-btn").addEventListener("click", () => openSheet("sort-sheet"));

    // Sheet options are keyboard-operable
    document.querySelectorAll(".sheet-option").forEach(opt => {
        if (opt.tagName !== "BUTTON") {
            opt.setAttribute("role", "button");
            opt.setAttribute("tabindex", "0");
        }
        opt.addEventListener("keydown", (e) => {
            if (e.key === "Enter" || e.key === " ") { e.preventDefault(); opt.click(); }
        });
    });

    // Speed selection
    document.querySelectorAll(".speed-option").forEach(opt => {
        opt.addEventListener("click", () => {
            setPlaybackSpeed(parseFloat(opt.dataset.speed));
            closeAllSheets();
        });
    });

    // Sleep timer options
    document.querySelectorAll(".sleep-option").forEach(opt => {
        opt.addEventListener("click", () => {
            startSleepTimer(parseInt(opt.dataset.mins));
            closeAllSheets();
        });
    });
    document.getElementById("cancel-sleep-btn").addEventListener("click", () => {
        cancelSleepTimer();
        closeAllSheets();
    });

    // Sort options
    document.querySelectorAll(".sort-option").forEach(opt => {
        opt.addEventListener("click", () => {
            document.querySelectorAll(".sort-option").forEach(o => o.classList.remove("active"));
            opt.classList.add("active");
            playbackState.sortOrder = opt.dataset.sort;
            renderLibrary();
            closeAllSheets();
        });
    });

    // Action Sheet items
    document.getElementById("action-play-next").addEventListener("click", () => {
        if (activeActionSong) {
            playbackState.queue.splice(playbackState.queueIndex + 1, 0, activeActionSong);
            closeAllSheets();
            updateUI();
        }
    });
    document.getElementById("action-add-queue").addEventListener("click", () => {
        if (activeActionSong) {
            playbackState.queue.push(activeActionSong);
            closeAllSheets();
            updateUI();
        }
    });
    document.getElementById("action-track-info").addEventListener("click", () => {
        if (activeActionSong) {
            closeAllSheets();
            const grid = document.getElementById("info-grid-container");
            grid.innerHTML = `
                <div class="info-row"><span class="info-label">Title</span><span class="info-value">${activeActionSong.title}</span></div>
                <div class="info-row"><span class="info-label">Artist</span><span class="info-value">${activeActionSong.artist}</span></div>
                <div class="info-row"><span class="info-label">Album</span><span class="info-value">${activeActionSong.album}</span></div>
                <div class="info-row"><span class="info-label">Duration</span><span class="info-value">${activeActionSong.formattedDuration}</span></div>
                <div class="info-row"><span class="info-label">Format</span><span class="info-value">${activeActionSong.format}</span></div>
                <div class="info-row"><span class="info-label">Bitrate</span><span class="info-value">${activeActionSong.bitrate || '320 kbps'}</span></div>
                <div class="info-row"><span class="info-label">File Size</span><span class="info-value">${activeActionSong.fileSize || '8.2 MB'}</span></div>
                <div class="info-row"><span class="info-label">File Path</span><span class="info-value" style="font-size:0.7rem; word-break:break-all;">${activeActionSong.path}</span></div>
            `;
            openSheet("track-info-sheet");
        }
    });

    // Overlay dismiss
    document.querySelectorAll(".bottom-sheet-overlay").forEach(overlay => {
        overlay.addEventListener("click", (e) => {
            if (e.target === overlay) closeAllSheets();
        });
    });

    // Escape closes any open sheet/modal
    document.addEventListener("keydown", (e) => {
        const fullPlayer = document.getElementById("full-player-modal");
        const anyOpen = document.querySelector(".bottom-sheet-overlay.open") ||
            (fullPlayer && fullPlayer.classList.contains("open"));
        if (e.key === "Escape" && anyOpen) {
            e.preventDefault();
            closeAllSheets();
        }
    });

    // Playlists modal
    document.getElementById("create-playlist-btn").addEventListener("click", () => {
        document.getElementById("playlist-name-input").value = "";
        openSheet("playlist-dialog-overlay");
    });
    document.getElementById("dialog-cancel-btn").addEventListener("click", closeAllSheets);
    document.getElementById("dialog-confirm-btn").addEventListener("click", () => {
        const name = document.getElementById("playlist-name-input").value.trim();
        if (name) {
            playlists.push({ id: Date.now(), name, songIds: [] });
            localStorage.setItem("sonqiva_playlists", JSON.stringify(playlists));
            closeAllSheets();
            updateUI();
        }
    });

    // Desktop Header Controls
    document.getElementById("toggle-frame-btn").addEventListener("click", () => {
        document.getElementById("device-frame").classList.toggle("full-width-mode");
    });
    document.getElementById("rescan-lib-btn").addEventListener("click", () => {
        localStorage.removeItem("sonqiva_songs");
        localStorage.removeItem("sonqiva_playlists");
        songs = [...INITIAL_SONGS];
        playlists = [
            { id: 1, name: "Chill Atmospheric", songIds: [1, 3] },
            { id: 2, name: "Synthwave Night Drive", songIds: [5, 6] }
        ];
        updateUI();
    });

    // Real Audio File Upload Handler
    document.getElementById("audio-file-input").addEventListener("change", (e) => {
        const files = Array.from(e.target.files);
        if (files.length === 0) return;

        files.forEach((file, index) => {
            const url = URL.createObjectURL(file);
            const nameWithoutExt = file.name.substring(0, file.name.lastIndexOf('.')) || file.name;
            const newSong = {
                id: Date.now() + index,
                title: nameWithoutExt,
                artist: "Local Storage",
                album: "Uploaded Tracks",
                albumId: 999,
                artistId: 999,
                durationMs: 210000,
                formattedDuration: "3:30",
                path: `/storage/emulated/0/Music/${file.name}`,
                folder: "Uploaded",
                fileSize: `${(file.size / (1024 * 1024)).toFixed(1)} MB`,
                format: file.type || "audio/mp3",
                bitrate: "Local Stream",
                isFavorite: false,
                audioBlobUrl: url
            };
            songs.unshift(newSong);
        });

        localStorage.setItem("sonqiva_songs", JSON.stringify(songs));
        updateUI();
        playSong(songs[0]);
    });

    // Initial render
    updateUI();
});
