import httpClient, { getRefreshToken, saveAccessToken, saveRefreshToken } from './httpClient'
import { unwrapApiResponse } from './apiResponse'

export async function login(credentials) {
  const response = await httpClient.post('/auth/login', credentials)
  const data = unwrapApiResponse(response)

  if (data?.accessToken) {
    saveAccessToken(data.accessToken)
  }

  if (data?.refreshToken) {
    saveRefreshToken(data.refreshToken)
  }

  return data
}

export async function register(payload) {
  const response = await httpClient.post('/auth/register', payload)
  return unwrapApiResponse(response)
}

export async function refreshToken() {
  const response = await httpClient.post('/auth/refresh-token', {
    refreshToken: getRefreshToken(),
  })
  const data = unwrapApiResponse(response)

  if (data?.accessToken) {
    saveAccessToken(data.accessToken)
  }

  if (data?.refreshToken) {
    saveRefreshToken(data.refreshToken)
  }

  return data
}
