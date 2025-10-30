package com.mattverwey.m3uplayer.data.model

data class XtreamCategory(
    val category_id: String,
    val category_name: String,
    val parent_id: Int? = 0
)

data class XtreamStream(
    val num: Int? = null,
    val name: String,
    val stream_type: String? = null,
    val stream_id: Int,
    val stream_icon: String? = null,
    val epg_channel_id: String? = null,
    val added: String? = null,
    val category_id: String? = null,
    val custom_sid: String? = null,
    val tv_archive: Int? = null,
    val direct_source: String? = null,
    val tv_archive_duration: Int? = null,
    val rating: String? = null,
    val rating_5based: Double? = null
)

data class XtreamVOD(
    val num: Int? = null,
    val name: String,
    val stream_type: String? = null,
    val stream_id: Int,
    val stream_icon: String? = null,
    val rating: String? = null,
    val rating_5based: Double? = null,
    val added: String? = null,
    val category_id: String? = null,
    val container_extension: String? = null,
    val custom_sid: String? = null,
    val direct_source: String? = null
)

data class XtreamVODInfo(
    val info: VODInfoDetails? = null,
    val movie_data: MovieData? = null
)

data class VODInfoDetails(
    val kinopoisk_url: String? = null,
    val tmdb_id: String? = null,
    val name: String? = null,
    val o_name: String? = null,
    val cover_big: String? = null,
    val movie_image: String? = null,
    val releasedate: String? = null,
    val episode_run_time: String? = null,
    val youtube_trailer: String? = null,
    val director: String? = null,
    val actors: String? = null,
    val cast: String? = null,
    val description: String? = null,
    val plot: String? = null,
    val age: String? = null,
    val mpaa_rating: String? = null,
    val rating_count_kinopoisk: Int? = null,
    val country: String? = null,
    val genre: String? = null,
    val duration_secs: Int? = null,
    val duration: String? = null,
    val video: Map<String, String>? = null,
    val audio: Map<String, String>? = null,
    val bitrate: Int? = null
)

data class MovieData(
    val stream_id: Int? = null,
    val name: String? = null,
    val added: String? = null,
    val category_id: String? = null,
    val container_extension: String? = null,
    val custom_sid: String? = null,
    val direct_source: String? = null
)

data class XtreamSeries(
    val num: Int? = null,
    val name: String,
    val series_id: Int,
    val cover: String? = null,
    val plot: String? = null,
    val cast: String? = null,
    val director: String? = null,
    val genre: String? = null,
    val releaseDate: String? = null,
    val last_modified: String? = null,
    val rating: String? = null,
    val rating_5based: Double? = null,
    val backdrop_path: List<String>? = null,
    val youtube_trailer: String? = null,
    val episode_run_time: String? = null,
    val category_id: String? = null
)

data class XtreamSeriesInfo(
    val seasons: List<SeriesSeason>? = null,
    val info: SeriesInfoDetails? = null,
    val episodes: Map<String, List<SeriesEpisode>>? = null
)

data class SeriesSeason(
    val air_date: String? = null,
    val episode_count: Int? = null,
    val id: Int? = null,
    val name: String? = null,
    val overview: String? = null,
    val season_number: Int,
    val cover: String? = null,
    val cover_big: String? = null
)

data class SeriesInfoDetails(
    val name: String? = null,
    val cover: String? = null,
    val plot: String? = null,
    val cast: String? = null,
    val director: String? = null,
    val genre: String? = null,
    val releaseDate: String? = null,
    val last_modified: String? = null,
    val rating: String? = null,
    val rating_5based: Double? = null,
    val backdrop_path: List<String>? = null,
    val youtube_trailer: String? = null,
    val episode_run_time: String? = null,
    val category_id: String? = null
)

data class SeriesEpisode(
    val id: String? = null,
    val episode_num: Int,
    val title: String? = null,
    val container_extension: String? = null,
    val info: EpisodeInfo? = null,
    val custom_sid: String? = null,
    val added: String? = null,
    val season: Int,
    val direct_source: String? = null
)

data class EpisodeInfo(
    val name: String? = null,
    val overview: String? = null,
    val air_date: String? = null,
    val rating: String? = null,
    val movie_image: String? = null
)
