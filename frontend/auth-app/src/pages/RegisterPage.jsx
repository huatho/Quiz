import { useState } from 'react'
import { register } from '../api/authApi'
import { getApiMessage } from '../api/httpClient'

function RegisterPage({ onRegisterSuccess }) {
  const [form, setForm] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
  })
  const [error, setError] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  const updateField = (event) => {
    setForm((currentForm) => ({
      ...currentForm,
      [event.target.name]: event.target.value,
    }))
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    setError('')

    if (!form.name.trim() || !form.email.trim() || !form.password) {
      setError('Please fill in all required fields.')
      return
    }

    if (form.password.length < 6) {
      setError('Password must be at least 6 characters.')
      return
    }

    if (form.password !== form.confirmPassword) {
      setError('Password confirmation does not match.')
      return
    }

    setIsSubmitting(true)

    try {
      await register({
        name: form.name.trim(),
        email: form.email.trim(),
        password: form.password,
      })
      onRegisterSuccess(form.email.trim())
    } catch (err) {
      setError(getApiMessage(err, 'Registration failed.'))
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <form className="space-y-5" onSubmit={handleSubmit}>
      <div>
        <label className="text-sm font-medium text-slate-700" htmlFor="name">
          Display name
        </label>
        <input
          id="name"
          name="name"
          type="text"
          value={form.name}
          onChange={updateField}
          className="input-control mt-2"
          autoComplete="name"
          required
        />
      </div>

      <div>
        <label className="text-sm font-medium text-slate-700" htmlFor="register-email">
          Email address
        </label>
        <input
          id="register-email"
          name="email"
          type="email"
          value={form.email}
          onChange={updateField}
          className="input-control mt-2"
          autoComplete="email"
          required
        />
      </div>

      <div>
        <label className="text-sm font-medium text-slate-700" htmlFor="register-password">
          Password
        </label>
        <input
          id="register-password"
          name="password"
          type="password"
          value={form.password}
          onChange={updateField}
          className="input-control mt-2"
          autoComplete="new-password"
          required
        />
      </div>

      <div>
        <label
          className="text-sm font-medium text-slate-700"
          htmlFor="confirm-password"
        >
          Confirm password
        </label>
        <input
          id="confirm-password"
          name="confirmPassword"
          type="password"
          value={form.confirmPassword}
          onChange={updateField}
          className="input-control mt-2"
          autoComplete="new-password"
          required
        />
      </div>

      {error && <p className="text-sm text-red-600">{error}</p>}

      <button
        type="submit"
        disabled={isSubmitting}
        className="btn btn-primary w-full"
      >
        {isSubmitting ? 'Creating account...' : 'Create account'}
      </button>
    </form>
  )
}

export default RegisterPage
