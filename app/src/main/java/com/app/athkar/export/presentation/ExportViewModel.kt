package com.app.athkar.export.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.athkar.core.util.CryptLib
import com.app.athkar.export.audio_downloader.AudioDownloaderService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val audioDownloaderService: AudioDownloaderService
): ViewModel() {

    private val _state = mutableStateOf(ExportState())
    val state: State<ExportState> = _state

    init {
        val encryptedText = "IN5Br7gp3wLGqsziH1aD2Up\\/X1Ltkv3TGFljxGVNQsapIm6LLvX+eI\\/CaqEPCsmWykGi0qQ2obKx7UVsUZ7nT0fjZ4tS603mXZJAt+r7iIBL0+On8gDdE9d\\/qT3EHqkdDKgkvjPuZ+oI3TD0+LqVW\\/V1ZDlwKMkLJoCOHVRBaFAzyqmnraG0qSp1HPI6HA5v4+3QqFqDc+ec7wnsiE9a6vm9wja0qfIOa7GdSQL3I0tbCuTBJSagWMOOzpGS22rYjwWENCS0tiB3ZoFmz+CBi2A6ssA7HYGCUcYwZbu8+rjSbZ4eCavY3TfkPvbIqcvlhyW1U4zR0OwnhFPWSPshJsg+7HqZgj5PRKI\\/wlTnI4T5TuNWolaDkPf36sAB\\/NUAYNI7xqzpQfWkttUq3t9LyTrjh1iUAN01OIK6ZKIkzdd\\/0KISIHcTia2asJd5RuHo7asNnxljPLCfr9TqauY26CrLGS6ZyK4XD70xIrZHeNEos7FR1bSNZtBDtVAEd3OVtmXm4Sdzr4n5nddsK8YrxUdM2WHJSaz+FqSLZwIks3o30dbQaymd9pCe2pKVWTXP\\/u86grz53ddyvzFzcP+J2uiYVvtBuk\\/PS\\/YGP7EczQ\\/A8WuPImBRGgnN0MCo53bHvzLaD+L5AP6M8tMApZ8hhOH+ZxGC2MoB84o7\\/WjHcM3d4ziKpSrSLGUjEiQk9JGzy1aQ2J79P\\/JxmDJvgn4BdQzKzwrhgT96XKw48+LyCikaYBT\\/+gS54w+SUAvMIh3rzWM9wihxUWXP0wqcX9MzVbe5c+dycr1kxAl6TSOFiahhcp+ern98pjeopNtuHB60Y\\/KXsv\\/Rd6sGz48mTO8rjmLnD5xvQMM4yi\\/vx3SbKNk0Kjq1JDx4rQ7SO2oBCYPQKLiFYUGI0aHHwQUwcXYpcFgM6v5y0Sq0jPa9uIUqqxd6g1Qcnrti\\/jj0CZ9qBL2l9KkGUrff2EbhEaELowS3mUKzjtIEZXEB0TCG+E4jYjtawdEFkT1aVWEVNnsNTHy86iuvhMkASXzfMKQRHDxEeWz3Rez9vPPUQ6\\/LW63Tqt\\/r2lpHesHSGBGm2O4TGT5Qqt0mLWq3Mx\\/sD5jOiooOyvMtQFkit3A\\/r4mnqIDOtlRibMmMQJhg\\/d1qe4Qf+0Tic5+xirdBIqH3fHS4B\\/7y6YNjUMlA6TTY\\/csPpfQJd937nxm2eNtTL8qJEa0xC4gfhH+n98ewIcSuftYUqu\\/BM0YRqQwQpxsacMnfHviVWc73ZXksv4lVyRCYD0kQNLRKrWWOXP9CMFqL0jheiSBZ2LaknOFeioAtzRzjdTu0CtaLI4NycenKpEXnFzlUhjFymNLbqcIxEXGdjcKMFE3GD5Mv+o0ahHvWFabiRh1KhE+VHLjRbq5Vw0B94ZLpIt5ObohvJzUeFrrvpGyj9kZMzv+LsUtohQEZA7NHjDB5mOTT5hqChrg4QxcQy5\\/Rzl9\\/pFy7wmmZupCCMMEZvq2dfP2hZgxBgEi6l2S1UhzvUo0IV55QligWRrnyT3qD1FnNIcCttGSBgE2NayXQgqj7ozX9ecmWWNg2ukDcyyCuukxB\\/1pZ1uIkuiDygwGKAUHUmCuw6a1BOvxIls5VjrTTIfQLA6mGxXFweVn1riNWobGDdOq5mhZ5U3H11iNL5K4ko6xAN0H1yvLYyo\\/YrSK8Rj\\/JSIWsbK5lTsk1vtp9g2bDROPsZ166Q4mDxjx8gdghVHBesr58Cm2Pq0sEcZimHWaiGzNWSCTUzyRLU3FXXsjKURal2ypZ2QZmoTE80CxM2+JUzc2R94oKwToD5IjaUVrsQqsP4jk1YgEAsK8bchhLfyIJXj7eL2z58xeo9COM\\/TNorAT5CjNcwv1wZLjMQqZGwWN795q0k\\/8Vby1C3eROTaHxDF\\/kJe92ldBhsqfAtiD4psfN913H6sCIepjpjSRTwCKed2PQgaN1NmiioaakLw56dY4R0MGkmMRZt9CqgtClCUKWndtqPmpIiNQJzBfOPk7LN0kMDE3ifmnRvrG3ZKDEtr4iUIqfSLJtik36Z3arCDtnHWtep79wD3XeOL61rl8laBbHtd1Oo1rcdkMAFmWdNOb4ee0ekUJomTMhHqAhTsHlI5axbo8pioepFv23dgFW3FCpwFGtHgbIvNwvTVPtnGeRwG9PdIIgNFEo4H4AZwNbuC2S\\/YRnrez+MI7cugCuVk4WhRjZwZW6yyqVRKGtLyoU6H9eHsu\\/ofYcuqmwxcJXO5bzMBrveufWqBCLb3pW7bt44FpYPpmcijTPB1Y9QjcuWRB1EYlWuob3CDki3pWn2AGFENwaK6vF7VIi5twlaG7hHCD31rTpQEFO8pllzcAr5viB9TexaPRCnCpVbcUhOsEI14wImpG9td9zKBckQ+3HbXy5FZzLvbeFaWD\\/8+AKNVFNaI\\/ae3qbadpz8f+b5xadHjXFpkK24f8Z0ePfoNZODmUaqLLX9ULFzYGBKEDUBeqcEaRZoGNkBuzCtAr4aZFGpucr3LPiC0nsHM4nY\\/25B7\\/Lsngl0ey6m0CFy4oNpLYFsXBYbPeCzVgbGoRQtuJ8lSaC4\\/VJvcjBdDuGurcoPrwGE0uVDlAz3HiPGJdWX3O4XsYa7gopnIjsO6+aB0sJmerMBmXESkxjZoKXeKBlkf88P+TTJtjFR8e2drUq9VGP8bcS55l+y6LRFqjiOJzpOccvlQtdXENv0ce9nMDjvxsD6RwPGI6fnW\\/W92EkphrvlTZ2vlx4IYKBdAjl5W\\/LkfeDS6VRmbrv7fopnNvBR+gmkqjjOE25O+x1r1ddWcLNWvzVI4kY+UdMurwGWBqlAfSzsGcJHSrtP9aCWjKoSvn\\/H+z\\/6AmYBzxXi4OgR87B9tlHEFadBZFOlXkjSvF63Q3109NH8irE\\/NYjGsmpof\\/JW5R1ogfGS96K5gDeErdpmdrMfYpwuuKocNaffYRnBLrlH41s8\\/gPRjAZsSslgzhKsK91P9QbMKwO9LBlGGmT6lqHT+xHXHZ5huFnC+o3ffVdAWMVYMkfcqk2\\/PgTRYshtIceHP1VuBp5AinoXGY3UOfrheP8aTM2iU+5VGcA8Em1zknRwe3ySw6y+UFKpaYADj6\\/QcxINqilfczN\\/PSzcB\\/yiOURhfXOOLpvTjyFlSxjsC\\/zv5kOavoLmHuVJ60T77+2CknHAU2Kh\\/bsChqzLycXf8Bv6GOszXP6T\\/ilzk\\/D84ZqD2x6eYHkGYErAIg4X1cgiQXFmxn4T4uoLkYG4psbxJhvgjE5FE8xJI\\/O410MCKWxAeEadjQEDZZow0vJ95QRimWcN6an+oVQXQ80DU6MCppewOfcbnz1jY1vuRIO+vaKN34kLHjS1jpIlSnB5S7Be1Av\\/mEBmKt97CID8TgMPyOlSHJBck9SfDEUC8oRjVK6wVRxxr7G3iFi4CUIdB8mvTZH0YlUUX9wvNK6tw\\/dlTrFaCAc0XQQuCs0dX7kL\\/hY2fHgsqwjnGFwGgX49RjucHgrFCQua6S1\\/VDlhNRkwRcCErMZE73FbLVftvrOPDdBi\\/B1UZcBETyzRpgnDX7UiZ61Bm\\/dgoH743I7S2+ehz2COHJ+2GCb\\/\\/Acp166L3RDY0rWW5isPx4nQ\\/ZmZxabndEDBFxzttEhg4S2DSAWVkThCghNtCrGnkiFo0eaaGxTrAIb\\/ijYX4jBfnyK\\/xfcO8XfbKyO4x3o066e3BbtxmS4xaqVn0jqLDcMZ0oi\\/7BTQ+Un5tyLKro4f53\\/HEVYI95z0mYsRVB3+3tysZXh5y2z7T5yQS1KAq53sby\\/A91KnZCnD7m4nwEJVrEdPMGLcH4Ip0JuY8h8sCa9ByWb5rsGeugyhUHA6HfLe7ITpguk1jZ4LzLgF2I1C5Fbgjv8nZgWPRnfqmOf0dgM9PozzB\\/a5QQF4l7nGG+P1HocsSu8+\\/Crti6hqc1QYxVL0WUwv4FGOKXkNNWb7iDafJrRl4JF"
        val decryptedText = CryptLib.decryptData(encryptedText)
        _state.value = ExportState(decryptedText)
    }

    fun onEvent(event: ExportViewModelEvent) {
        when(event) {
            ExportViewModelEvent.Pause -> {}
            ExportViewModelEvent.Play -> {}
            is ExportViewModelEvent.Download -> {
                viewModelScope.launch {
                    _state.value = ExportState("Downloading...")
                    val downloadedFile = audioDownloaderService.downloadAudio(event.downloadUrl)
                    downloadedFile?.let {
                        _state.value = ExportState("Downloaded to $it")
                    } ?: run {
                        _state.value = ExportState("Failed to download")
                    }
                }
            }
        }
    }
}