import httpClient from './httpClient'
import { unwrapApiResponse } from './apiResponse'

export async function getExams() {
  const response = await httpClient.get('/exams')
  return unwrapApiResponse(response) || []
}

export async function getExamDetail(examId) {
  const response = await httpClient.get(`/exams/${examId}`)
  return unwrapApiResponse(response)
}

export async function createExam(payload) {
  const response = await httpClient.post('/exams', payload)
  return unwrapApiResponse(response)
}

export async function updateExam(examId, payload) {
  const response = await httpClient.put(`/exams/${examId}`, payload)
  return unwrapApiResponse(response)
}

export async function deleteExam(examId) {
  const response = await httpClient.delete(`/exams/${examId}`)
  return unwrapApiResponse(response)
}
