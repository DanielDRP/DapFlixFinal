let yelmoMoviesCount = 0;
let multicinesMoviesCount = 0;
let villaOrotavaMoviesCount = 0;
let comparativaChart = null;

function activateTab(tabName) {
    const tabs = document.querySelectorAll('.tab');
    tabs.forEach(tab => tab.classList.remove('active'));

    const panels = document.querySelectorAll('.panel');
    panels.forEach(panel => panel.classList.remove('active'));

    const activeTab = document.querySelector(`.tab[data-tab="${tabName}"]`);
    const activePanel = document.getElementById(tabName);

    if (activeTab && activePanel) {
        activeTab.classList.add('active');
        activePanel.classList.add('active');
    }
}

document.querySelectorAll('.tab').forEach(tab => {
    tab.addEventListener('click', () => {
        const tabName = tab.getAttribute('data-tab');
        activateTab(tabName);
    });
});

function updateComparativaChart() {
    const ctx = document.getElementById("comparativaChart").getContext("2d");

    if (comparativaChart) {
        comparativaChart.data.datasets[0].data = [yelmoMoviesCount, multicinesMoviesCount, villaOrotavaMoviesCount];
        comparativaChart.update();
        return;
    }

    comparativaChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['Yelmo Meridiano', 'Multicines Tenerife', 'Yelmo La Villa de la Orotava'],
            datasets: [{
                label: 'Películas en cartelera',
                data: [yelmoMoviesCount, multicinesMoviesCount, villaOrotavaMoviesCount],
                backgroundColor: ['rgba(75, 192, 192, 0.6)', 'rgba(255, 99, 132, 0.6)', 'rgba(153, 102, 255, 0.6)'],
                borderColor: ['rgba(75, 192, 192, 1)', 'rgba(255, 99, 132, 1)', 'rgba(153, 102, 255, 1)'],
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                y: { beginAtZero: true }
            },
            plugins: {
                tooltip: {
                    callbacks: {
                        label: function(tooltipItem) {
                            const datasetIndex = tooltipItem.datasetIndex;
                            const label = tooltipItem.label;
                            const count = tooltipItem.raw;
                            return `${count} películas`;
                        }
                    }
                },
                legend: {
                    position: 'top',
                    labels: {
                        generateLabels: function(chart) {
                            return [
                                { text: `Yelmo Meridiano`, fillStyle: 'rgba(75, 192, 192, 1)' },
                                { text: `Multicines Tenerife`, fillStyle: 'rgba(255, 99, 132, 1)' },
                                { text: `Yelmo La Villa`, fillStyle: 'rgba(153, 102, 255, 1)' }
                            ];
                        }
                    }
                }
            }
        }
    });
}

function updateMoviesCount() {
    const totalMoviesElement = document.getElementById("peliculas-en-cartelera");

    fetch('http://localhost:8080/api/dashboard/moviescount')
        .then(response => {
            if (!response.ok) {
                throw new Error("Error al obtener el número de películas");
            }
            return response.json();
        })
        .then(data => {
            const totalMovies = parseInt(data.content, 10);
            if (!isNaN(totalMovies)) {
                totalMoviesElement.textContent = totalMovies;
            } else {
                console.error("Respuesta no válida del servidor:", data);
            }
        })
        .catch(error => {
            console.error("Error al actualizar el número de películas:", error);
            totalMoviesElement.textContent = "Error al cargar";
        });
}

function loadAllMovies() {
    const cineSection = document.getElementById("cineMovies");
    const loadingIndicator = document.getElementById("loadingIndicator");

    cineSection.innerHTML = '';
    loadingIndicator.style.display = 'block';

    Promise.all([
        fetch('http://localhost:8080/api/movies/yelmo-meridiano').then(res => res.json()),
        fetch('http://localhost:8080/api/movies/multicines-tenerife').then(res => res.json()),
        fetch('http://localhost:8080/api/movies/la-villa-de-orotava').then(res => res.json())
    ])
    .then(([yelmoMovies, multicinesMovies, villaOrotavaMovies]) => {
        yelmoMoviesCount = yelmoMovies.length;
        multicinesMoviesCount = multicinesMovies.length;
        villaOrotavaMoviesCount = villaOrotavaMovies.length;

        const createCinemaSection = (cinemaName, movies) => {
            const sectionTitle = document.createElement("h2");
            sectionTitle.classList.add("cinema-title");
            sectionTitle.textContent = cinemaName;
            cineSection.appendChild(sectionTitle);

            movies.forEach(movie => {
                const movieElement = document.createElement("div");
                movieElement.classList.add("movie-item");
                movieElement.innerHTML = `<img src="${movie.image}" alt="${movie.title}"><div class="movie-title">${movie.title}</div>`;
                cineSection.appendChild(movieElement);
            });
        };

        createCinemaSection("Yelmo Cines", yelmoMovies);
        createCinemaSection("Multicines Tenerife", multicinesMovies);
        createCinemaSection("La Villa de la Orotava", villaOrotavaMovies);

        updateMoviesCount();
        updateComparativaChart();
        loadRanking();
    })
    .catch(error => console.error("Error al cargar las películas:", error))
    .finally(() => {
        loadingIndicator.style.display = 'none';
    });
}

function loadNetflixMovies() {
    fetch('http://localhost:8080/api/movies/netflix')
        .then(response => response.json())
        .then(data => {
            const streamingSection = document.getElementById("streamingMovies");
            streamingSection.innerHTML = '';
            document.getElementById("peliculas-streaming").textContent = data.length;

            data.forEach(movie => {
                const movieElement = document.createElement("div");
                movieElement.classList.add("movie-item");
                movieElement.innerHTML = `<img src="${movie.image}" alt="${movie.title}"><div class="movie-title">${movie.title}</div>`;
                streamingSection.appendChild(movieElement);
            });
        })
        .catch(error => console.error("Error al cargar películas de Netflix:", error));
}

function loadRanking() {
    fetch('http://localhost:8080/api/dashboard/year-ranking')
        .then(response => {
            if (!response.ok) {
                throw new Error("Error al obtener los datos del ranking");
            }
            return response.json();
        })
        .then(data => {
            if (Array.isArray(data)) {
                const movies = data.map(movie => ({
                    title: movie.title,
                    revenue: movie.info
                }));
                updateRankingTable(movies);
            } else {
                throw new Error("Formato de datos inesperado: la respuesta no es un array.");
            }
        })
        .catch(error => {
            console.error("Error al cargar el ranking:", error);
        });
}

function updateRankingTable(movies) {
    const rankingTable = document.getElementById("rankingTableBody");
    rankingTable.innerHTML = '';

    movies.forEach((movie, index) => {
        const row = document.createElement("tr");

        const positionCell = document.createElement("td");
        positionCell.textContent = index + 1;
        row.appendChild(positionCell);

        const titleCell = document.createElement("td");
        titleCell.textContent = movie.title;
        row.appendChild(titleCell);

        const revenueCell = document.createElement("td");
        revenueCell.textContent = movie.revenue;
        row.appendChild(revenueCell);

        rankingTable.appendChild(row);
    });
}

function updateMostViewedMovie() {
    fetch('http://localhost:8080/api/dashboard/most-viewed')
        .then(response => {
            if (!response.ok) {
                throw new Error("Error al obtener la película más vista");
            }
            return response.json();
        })
        .then(data => {
            if (data && data.title) {
                const mostViewedDiv = document.getElementById("mas-vista");
                mostViewedDiv.innerHTML = `
                    <h2>${data.title}</h2>
                    <p>Más vista esta semana</p>
                `;
            } else {
                const mostViewedDiv = document.getElementById("mas-vista");
                mostViewedDiv.innerHTML = `
                    <h2>Información no disponible</h2>
                    <p>No se pudo obtener la película más vista esta semana</p>
                `;
                console.error("La respuesta no contiene el campo 'title'", data);
            }
        })
        .catch(error => {
            console.error("Error al obtener la película más vista:", error);
            const mostViewedDiv = document.getElementById("mas-vista");
            mostViewedDiv.innerHTML = `
                <h2>Error</h2>
                <p>No se pudo obtener la película más vista</p>
            `;
        });
}

window.onload = function() {
    updateMostViewedMovie();
    loadRanking();
    loadAllMovies();
};

document.getElementById("loadMoviesBtn").addEventListener("click", loadAllMovies);
document.getElementById("netflixBtn").addEventListener("click", loadNetflixMovies);
activateTab('cine');