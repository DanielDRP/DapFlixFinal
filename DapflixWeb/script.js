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

function loadDisneyMovies() {
    fetch('http://localhost:8080/api/movies/disneyplus')
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
        .catch(error => console.error("Error al cargar películas de Disney:", error));
}

function loadMaxMovies() {
    fetch('http://localhost:8080/api/movies/max')
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
        .catch(error => console.error("Error al cargar películas de Max:", error));
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
/*function loadHistoricalRanking() {
    const ctx = document.getElementById("historicalPieChart").getContext("2d");

    fetch('http://localhost:8080/api/dashboard/historical-ranking')
        .then(response => {
            if (!response.ok) {
                throw new Error("Error al obtener los datos del ranking histórico");
            }
            return response.json();
        })
        .then(data => {
            if (Array.isArray(data)) {
                const titles = data.map(movie => movie.title);
                const revenues = data.map(movie => parseFloat(movie.info.replace(/,/g, '')));

                new Chart(ctx, {
                    type: 'pie',
                    data: {
                        labels: titles,
                        datasets: [{
                            label: 'Recaudación (€)',
                            data: revenues,
                            backgroundColor: [
                                'rgba(75, 192, 192, 0.6)',
                                'rgba(255, 99, 132, 0.6)',
                                'rgba(153, 102, 255, 0.6)',
                                'rgba(255, 205, 86, 0.6)',
                                'rgba(54, 162, 235, 0.6)'
                            ],
                            borderColor: [
                                'rgba(75, 192, 192, 1)',
                                'rgba(255, 99, 132, 1)',
                                'rgba(153, 102, 255, 1)',
                                'rgba(255, 205, 86, 1)',
                                'rgba(54, 162, 235, 1)'
                            ],
                            borderWidth: 1
                        }]
                    },
                    options: {
                        plugins: {
                            legend: {
                                position: 'top',
                            },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        const label = context.label || '';
                                        const value = context.raw || 0;
                                        return `${label}: €${value.toLocaleString()}`;
                                    }
                                }
                            }
                        }
                    }
                });
            } else {
                throw new Error("Formato de datos inesperado: la respuesta no es un array.");
            }
        })
        .catch(error => {
            console.error("Error al cargar el ranking histórico:", error);
        });
}*/

function loadYearRevenueChart() {
    const ctx = document.getElementById("historicalPieChart").getContext("2d");

    fetch('http://localhost:8080/api/dashboard/year-revenue')
        .then(response => {
            if (!response.ok) {
                throw new Error("Error al obtener los datos anuales");
            }
            return response.json();
        })
        .then(data => {
            if (Array.isArray(data) && data.length > 0) {
                const labels = data.map(item => item[0]);
                const values = data.map(item => parseFloat(item[1].replace("M€", "").trim()));

                new Chart(ctx, {
                    type: 'pie',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Recaudación 2025 (€)',
                            data: values,
                            backgroundColor: ['rgba(75, 192, 192, 0.6)', 'rgba(255, 99, 132, 0.6)'],
                            borderColor: ['rgba(75, 192, 192, 1)', 'rgba(255, 99, 132, 1)'],
                            borderWidth: 1
                        }]
                    },
                    options: {
                        plugins: {
                            legend: {
                                position: 'top',
                            },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        const label = context.label || '';
                                        const value = context.raw || 0;
                                        return `${label}: ${value.toFixed(2)}M€`;
                                    }
                                }
                            }
                        }
                    }
                });
            } else {
                throw new Error("Datos inválidos recibidos del servidor.");
            }
        })
        .catch(error => {
            console.error("Error al cargar el gráfico de recaudación anual:", error);
        });
}

let platformsChart = null;

function updatePlatformsChart(netflixCount, disneyCount, maxCount) {
    const ctx = document.getElementById("platformsChart").getContext("2d");

    if (platformsChart) {
        platformsChart.data.datasets[0].data = [netflixCount, disneyCount, maxCount];
        platformsChart.update();
        return;
    }

    platformsChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['Netflix', 'Disney+', 'Max'],
            datasets: [{
                label: 'Peliculas por plataforma',
                data: [netflixCount, disneyCount, maxCount],
                backgroundColor: ['rgba(255, 99, 132, 0.6)', 'rgba(54, 162, 235, 0.6)', 'rgba(75, 192, 192, 0.6)'],
                borderColor: ['rgba(255, 99, 132, 1)', 'rgba(54, 162, 235, 1)', 'rgba(75, 192, 192, 1)'],
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                y: { beginAtZero: true }
            }
        }
    });
}

function fetchPlatformCounts() {
    Promise.all([
        fetch('http://localhost:8080/api/movies/count/netflix').then(response => response.json()),
        fetch('http://localhost:8080/api/movies/count/disneyplus').then(response => response.json()),
        fetch('http://localhost:8080/api/movies/count/max').then(response => response.json())
    ])
    .then(data => {
        const netflixCount = data[0].content;
        const disneyCount = data[1].content;
        const maxCount = data[2].content;

        // Llamar a la función para actualizar el gráfico con los datos obtenidos
        updatePlatformsChart(netflixCount, disneyCount, maxCount);
    })
    .catch(error => console.error('Error al obtener los conteos de películas:', error));
}

// Llamar a la función cuando sea necesario, por ejemplo al cargar la página


function loadAllStreamingMovies() {
    fetchPlatformCounts();
    const streamingSection = document.getElementById("streamingMovies");
    const loadingIndicator = document.getElementById("loadingIndicator");

    streamingSection.innerHTML = '';
    loadingIndicator.style.display = 'block';

    Promise.all([
        fetch('http://localhost:8080/api/movies/netflix').then(res => res.json()),
        fetch('http://localhost:8080/api/movies/disneyplus').then(res => res.json()),
        fetch('http://localhost:8080/api/movies/max').then(res => res.json())
    ])
    .then(([netflixMovies, disneyMovies, maxMovies]) => {

        const createPlatformSection = (platformName, movies) => {
            const sectionTitle = document.createElement("h2");
            sectionTitle.classList.add("platform-title");
            sectionTitle.textContent = platformName;
            streamingSection.appendChild(sectionTitle);

            movies.forEach(movie => {
                const movieElement = document.createElement("div");
                movieElement.classList.add("movie-item");
                movieElement.innerHTML = `<img src="${movie.image}" alt="${movie.title}"><div class="movie-title">${movie.title}</div>`;
                streamingSection.appendChild(movieElement);
            });
        };

        createPlatformSection("Netflix", netflixMovies);
        createPlatformSection("Disney+", disneyMovies);
        createPlatformSection("Max", maxMovies);

        updatePlatformsChart(netflixCount, disneyCount, maxCount);
    })
    .catch(error => console.error("Error al cargar películas de plataformas:", error))
    .finally(() => {
        loadingIndicator.style.display = 'none';
    });
}

document.getElementById("loadAllPlatformsBtn").addEventListener("click", loadAllStreamingMovies);

window.onload = function() {
    updateMostViewedMovie();
    loadRanking();
    loadAllMovies();
    loadYearRevenueChart(); // Cargar el gráfico de tartas
};

document.getElementById("loadMoviesBtn").addEventListener("click", loadAllMovies);
document.getElementById("netflixBtn").addEventListener("click", loadNetflixMovies);
document.getElementById("disneyPlusBtn").addEventListener("click", loadDisneyMovies);
document.getElementById("maxBtn").addEventListener("click", loadMaxMovies);
activateTab('cine');
