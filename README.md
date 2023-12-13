# Редактор плейлистов Playlist Maker

Проект выполнен в качестве учебного и представляет небоольшое приложение для создания и редактирования плейлистов, позволяющее просматривать подробную информацию о треках, а также слушать отрывки выбранных треков.
В качестве базы аудиозаписей используется сервис iTunes, который позволяет искать по всей публично доступной библиотеке iTunes. 

Playlist Maker предоставляет следующие возможности:
- поиск треков в общедоступной базе iTunes;
- просмотр подробной информации о треке, прослушивание отрывка
- создание и редактирование плейлистов
- создание списка избранных треков

### Поиск треков

Поиск осуществляется по непустому набору слов поискового запроса.
Результаты поиска представляют собой список, содержащий краткую информацию о треках.

### Просмотр информации о треке

Из списка результатов поиска поиска можно перейти на экран аудиоплеера, содержащего 
информацию об исполнителе, альбоме, жанре трека. Также можно прослушать отрывок трека,
добавить трек в новый или существующий плейлист, либо добавить трек в избранное.

### Создание и редактирование плейлистов

Приложение позволяет добавлять плейлисты треков. Созданные плейлисты хранятся в 
локальной базе данных и доступны для просмотра краткой информации без подключения 
к интернету.

### Избранное

Отдельные треки можно добавлять в список избранного, который также хранится локально и доступен в 
любой момент.

## Общие требования

Приложение поддерживает устройства, начиная с Android 10 (minSdkVersion = 29)

## Стек технологий

Kotlin, Jetpack Navigation Component, ViewModel, Okhttp, Retrofit2, Room, Coroutines Flow, Koin, LiveData, RecyclerView,SharedPreferences, BottomNavigationView, Fragment