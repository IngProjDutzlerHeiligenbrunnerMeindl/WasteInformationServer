const cacheName = 'static-v2';
const staticAssets = [
    '/',
    '/index.html',
    '/css/index.css',
    '/js/index.js',
    '/manifest.json',
    '/lib/bootstrap.min.css',
    '/lib/bootstrap.min.js',
    '/lib/jquery.min.js',
    '/lib/popper.min.js',
    '/rsc/login2.jpg',
    '/favicon.png'
];

self.addEventListener('install', async e => {
    console.log('V1 installing…');

    e.waitUntil(
        caches.open('static-v2').then(cache => cache.addAll(staticAssets))
    );

    // const cache = await caches.open(cacheName);
    // await cache.addAll(staticAssets);
    // return self.skipWaiting();
});

self.addEventListener('activate', e => {
    self.clients.claim();
});

self.addEventListener('fetch', async e => {
    const req = e.request;
    const url = new URL(req.url);

    if (url.origin === location.origin) {
        e.respondWith(cacheFirst(req));
    } else {
        e.respondWith(networkAndCache(req));
    }
});

async function cacheFirst(req) {
    const cache = await caches.open(cacheName);
    const cached = await cache.match(req);
    return cached || fetch(req);
}

async function networkAndCache(req) {
    const cache = await caches.open(cacheName);
    try {
        const fresh = await fetch(req);
        await cache.put(req, fresh.clone());
        return fresh;
    } catch (e) {
        const cached = await cache.match(req);
        return cached;
    }
}