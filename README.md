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

<img src="https://github.com/Elen-B/PlaylistMaker/assets/118533717/d59c5123-819d-4adb-8595-91f66649b290" height="30%" width="30%"/>
<img src="https://github.com/Elen-B/PlaylistMaker/assets/118533717/38817eac-47c1-4837-bb26-163718cfa0ed" height="30%" width="30%"/>

### Просмотр информации о треке

Из списка результатов поиска поиска можно перейти на экран аудиоплеера, содержащего 
информацию об исполнителе, альбоме, жанре трека. Также можно прослушать отрывок трека,
добавить трек в новый или существующий плейлист, либо добавить трек в избранное.  

<img src="https://github.com/Elen-B/PlaylistMaker/assets/118533717/db5e5c8c-527a-498c-a517-0c248a00d24b" height="30%" width="30%"/>
<img src="https://github.com/Elen-B/PlaylistMaker/assets/118533717/fc7f0a50-7662-4956-ae11-4034c552bc6d" height="30%" width="30%"/>

### Создание и редактирование плейлистов

Приложение позволяет добавлять плейлисты треков. Созданные плейлисты хранятся в 
локальной базе данных и доступны для просмотра краткой информации без подключения 
к интернету.  

<img src="https://github.com/Elen-B/PlaylistMaker/assets/118533717/c74483ff-11fb-40e3-8ad6-3b46d886de71" height="30%" width="30%"/>
<img src="https://github.com/Elen-B/PlaylistMaker/assets/118533717/736feee6-982b-4e7f-ac52-c27e117ef122" height="30%" width="30%"/>
</br>
<img src="https://github.com/Elen-B/PlaylistMaker/assets/118533717/6258e762-1fb9-4847-a154-196a1d9acc7a" height="30%" width="30%"/>
<img src="https://github.com/Elen-B/PlaylistMaker/assets/118533717/4c42fd72-29be-49bf-87d3-983f1657250f" height="30%" width="30%"/>

### Избранное

Отдельные треки можно добавлять в список избранного, который также хранится локально и доступен в 
любой момент.

<img src="https://github.com/Elen-B/PlaylistMaker/assets/118533717/fd4895ce-b0e8-4b91-a894-9fa8ee8d4521" height="30%" width="30%"/>

## Общие требования

Приложение поддерживает устройства, начиная с Android 10 (minSdkVersion = 29)

## Стек технологий

Kotlin, Jetpack Navigation Component, ViewModel, Okhttp, Retrofit2, Room, Coroutines Flow, Koin, LiveData, RecyclerView,SharedPreferences, BottomNavigationView, Fragment
