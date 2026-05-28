import httpClient from './httpClient'
import { unwrapApiResponse } from './apiResponse'

export async function changePassword(payload) {
  const response = await httpClient.put('/users/me/password', payload)
  return unwrapApiResponse(response)
}
