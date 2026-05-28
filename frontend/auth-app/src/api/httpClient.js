import axios from 'axios'

const ACCESS_TOKEN_KEY = 'accessToken'
const REFRESH_TOKEN_KEY = 'refreshToken'
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

let isRefreshing = false
let refreshPromise = null

function getRequestPath(url) {
  try {
    return new URL(url, API_BASE_URL).pathname
  } catch {
    return url?.split('?')[0] || ''
  }
}

function isAuthEndpoint(url) {
  return getRequestPath(url).startsWith('/auth/')
}

const httpClient = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
})

httpClient.interceptors.request.use((config) => {
  const accessToken = localStorage.getItem(ACCESS_TOKEN_KEY)

  if (accessToken && !isAuthEndpoint(config.url)) {
    config.headers.Authorization = `Bearer ${accessToken}`
  }

  return config
})

httpClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    if (
      error.response?.status !== 401 ||
      originalRequest?._retry ||
      isAuthEndpoint(originalRequest?.url)
    ) {
      return Promise.reject(error)
    }

    originalRequest._retry = true

    try {
      if (!isRefreshing) {
        isRefreshing = true
        const refreshToken = getRefreshToken()

        if (!refreshToken) {
          clearAuthTokens()
          return Promise.reject(error)
        }

        refreshPromise = httpClient.post('/auth/refresh-token', { refreshToken })
      }

      const response = await refreshPromise
      const accessToken = response.data?.data?.accessToken
      const refreshToken = response.data?.data?.refreshToken

      if (accessToken) {
        saveAccessToken(accessToken)
        originalRequest.headers = originalRequest.headers || {}
        originalRequest.headers.Authorization = `Bearer ${accessToken}`
      }

      if (refreshToken) {
        saveRefreshToken(refreshToken)
      }

      return httpClient(originalRequest)
    } catch (refreshError) {
      clearAuthTokens()
      return Promise.reject(refreshError)
    } finally {
      isRefreshing = false
      refreshPromise = null
    }
  },
)

export function getApiMessage(error, fallbackMessage = 'Something went wrong') {
  return error?.response?.data?.message || fallbackMessage
}

export function getAccessToken() {
  return localStorage.getItem(ACCESS_TOKEN_KEY)
}

export function getRefreshToken() {
  return localStorage.getItem(REFRESH_TOKEN_KEY)
}

export function saveAccessToken(accessToken) {
  localStorage.setItem(ACCESS_TOKEN_KEY, accessToken)
}

export function saveRefreshToken(refreshToken) {
  localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken)
}

export function clearAccessToken() {
  localStorage.removeItem(ACCESS_TOKEN_KEY)
}

export function clearAuthTokens() {
  localStorage.removeItem(ACCESS_TOKEN_KEY)
  localStorage.removeItem(REFRESH_TOKEN_KEY)
}

export default httpClient
