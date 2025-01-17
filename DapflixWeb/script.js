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

        // Llamar a loadAllStreamingMovies y loadPlatformsChartTv cuando la pestaña "streaming" sea seleccionada
        if (tabName === "streaming") {
            loadAllStreamingMovies();
            loadPlatformsChartTv();
            loadPlatformsChartTvMock();
        }
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
    fetchPlatformCounts();  // Obtener los conteos y actualizar el gráfico de plataformas
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

        // Ya se ha actualizado el gráfico de plataformas en fetchPlatformCounts()
    })
    .catch(error => console.error("Error al cargar películas de plataformas:", error))
    .finally(() => {
        loadingIndicator.style.display = 'none';
    });
}
// Función para realizar la solicitud a la API y actualizar el título de la película más vista
function fetchMostViewedSpanishMovie() {
    // Realizamos la solicitud a la API para obtener la película más vista
    fetch('http://localhost:8080/api/dashboard/most-viewed-spanish')
        .then(response => response.json()) // Convierte la respuesta en formato JSON
        .then(data => {
            // Extraemos el título de la película más vista desde la respuesta, si está disponible
            const mostViewedMovieTitle = data.title;  // Asumiendo que la API devuelve un campo 'title'

            // Actualizamos el título de la película más vista en el HTML
            document.getElementById('titulo-pelicula').innerText = mostViewedMovieTitle;
        })
        .catch(error => {
            // Si hay un error, mostramos un mensaje
            console.error('Error al obtener la película más vista:', error);
        });
}

function loadPlatformsChartTv() {
    const ctxTv = document.getElementById('platformsChartTv').getContext('2d');
    console.log("Inicializando la carga de datos para la gráfica de TV...");

    Promise.all([
        fetch('http://localhost:8080/api/movies/count/netflixtv').then(res => res.json()),
        fetch('http://localhost:8080/api/movies/count/disneytv').then(res => res.json()),
        fetch('http://localhost:8080/api/movies/count/maxtv').then(res => res.json())
    ])
        .then(([netflixTv, disneyTv, maxTv]) => {
            console.log("Datos obtenidos de las APIs:", netflixTv, disneyTv, maxTv);

            // Validar que los datos están disponibles
            const netflixCount = parseInt(netflixTv.content || 0, 10);
            const disneyCount = parseInt(disneyTv.content || 0, 10);
            const maxCount = parseInt(maxTv.content || 0, 10);
            console.log("Datos procesados:", { netflixCount, disneyCount, maxCount });

            // Crear o actualizar el gráfico
            if (platformsChart) {
                platformsChart.data.datasets[0].data = [netflixCount, disneyCount, maxCount];
                platformsChart.update();
            } else {
                platformsChart = new Chart(ctxTv, {
                    type: 'bar',
                    data: {
                        labels: ['Netflix TV', 'Disney+ TV', 'Max TV'],
                        datasets: [{
                            label: 'Número de Series',
                            data: [netflixCount, disneyCount, maxCount],
                            backgroundColor: [
                                'rgba(255, 99, 132, 0.6)',
                                'rgba(54, 162, 235, 0.6)',
                                'rgba(75, 192, 192, 0.6)'
                            ],
                            borderColor: [
                                'rgba(255, 99, 132, 1)',
                                'rgba(54, 162, 235, 1)',
                                'rgba(75, 192, 192, 1)'
                            ],
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: {
                                display: true,
                                position: 'top',
                            }
                        },
                        scales: {
                            y: {
                                beginAtZero: true
                            }
                        }
                    }
                });
            }
        })
        .catch(err => {
            console.error("Error al obtener datos para la gráfica de TV:", err);
        });
}


function loadPlatformsChartTvMock() {
    const ctxTv = document.getElementById('platformsChartTv').getContext('2d');

    const netflixCount = 10;
    const disneyCount = 5;
    const maxCount = 8;

    platformsChart = new Chart(ctxTv, {
        type: 'bar',
        data: {
            labels: ['Netflix TV', 'Disney+ TV', 'Max TV'],
            datasets: [{
                label: 'Número de Series',
                data: [netflixCount, disneyCount, maxCount],
                backgroundColor: [
                    'rgba(255, 99, 132, 0.6)',
                    'rgba(54, 162, 235, 0.6)',
                    'rgba(75, 192, 192, 0.6)'
                ],
                borderColor: [
                    'rgba(255, 99, 132, 1)',
                    'rgba(54, 162, 235, 1)',
                    'rgba(75, 192, 192, 1)'
                ],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                }
            },
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}





// Llamamos a la función cuando la página esté cargada
document.addEventListener('DOMContentLoaded', function() {
    fetchMostViewedSpanishMovie();
});

window.onload = function() {
    updateMostViewedMovie();
    loadRanking();
    loadAllMovies();
    loadYearRevenueChart(); // Cargar el gráfico de tartas
    loadAllStreamingMovies();  // Cargar las películas y actualizar el gráfico de plataformas
};


document.getElementById("loadMoviesBtn").addEventListener("click", loadAllMovies);
document.getElementById("netflixBtn").addEventListener("click", loadNetflixMovies);
document.getElementById("disneyPlusBtn").addEventListener("click", loadDisneyMovies);
document.getElementById("maxBtn").addEventListener("click", loadMaxMovies);
activateTab('cine');
